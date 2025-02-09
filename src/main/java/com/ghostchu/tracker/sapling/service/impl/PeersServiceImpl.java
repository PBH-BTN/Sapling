package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ghostchu.tracker.sapling.entity.Peers;
import com.ghostchu.tracker.sapling.mapper.PeersMapper;
import com.ghostchu.tracker.sapling.model.AnnounceRequest;
import com.ghostchu.tracker.sapling.model.ScrapePeers;
import com.ghostchu.tracker.sapling.service.IPeersService;
import com.ghostchu.tracker.sapling.service.IUserTaskRecordsService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.tracker.PeerEvent;
import com.github.yulichang.base.MPJBaseServiceImpl;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Slf4j
@Service
public class PeersServiceImpl extends MPJBaseServiceImpl<PeersMapper, Peers> implements IPeersService {
    @Autowired
    private IUserTaskRecordsService userTaskRecordsService;
    @Autowired
    private IUsersService usersService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public List<Peers> fetchPeers(long userId, long torrentId, int limit, @Nullable Integer specificIpProtocolVersion) {
        var wrapper = new QueryWrapper<Peers>()
                .eq("torrent", torrentId)
                .orderByAsc("RANDOM()")
                .last("LIMIT " + limit);
        if (specificIpProtocolVersion != null) {
            wrapper = wrapper.eq("family(ip) = {0}", specificIpProtocolVersion);
        }
        wrapper = wrapper.ne("owner", userId);
        return list(wrapper);
    }

    @Override
    public ScrapePeers scrape(long torrentId) {
        QueryWrapper<Peers> wrapper = new QueryWrapper<>();
        wrapper.select(
                        "COUNT(*) FILTER (WHERE to_go = 0) as seeds",
                        "COUNT(*) FILTER (WHERE to_go <> 0) as leeches"
                )
                .eq("torrent", torrentId); // 添加过滤条件
        var result = getMap(wrapper);
        if (result.isEmpty()) {
            return new ScrapePeers(0, 0);
        }
        return new ScrapePeers((int) result.get("seeds"), (int) result.get("leeches"));
    }

    @Override
    @Transactional
    public void announce(List<AnnounceRequest> requests) {
        OffsetDateTime now = OffsetDateTime.now();
        for (AnnounceRequest request : requests) {
            Peers peers = this.baseMapper.selectPeersForUpdateByIp(request.torrentId(), request.userId(), request.peerId(), request.peerIp());
            if (peers == null) {
                // 初始化新建数据
                peers = new Peers();
                peers.setTorrent(request.torrentId());
                peers.setOwner(request.userId());
                peers.setPeerId(request.peerId());
                peers.setIp(request.peerIp());
                peers.setPort(request.port());
                peers.setStarted(now);
                peers.setLastAnnounce(now);
                peers.setLastAction(request.peerEvent().toString());
                peers.setToGo(request.left());
                peers.setUploadOffset(request.uploaded());
                peers.setDownloadOffset(request.downloaded());
                peers.setUploaded(peers.getUploaded());
                peers.setDownloaded(peers.getDownloaded());
                peers.setConnectable(null);
                peers.setUserAgent(request.ua());
                peers.setReqIp(request.reqIpInetAddress());
            }
            OffsetDateTime previousAnnounce = peers.getLastAnnounce();
            PeerEvent previousEvent = PeerEvent.valueOf(peers.getLastAction());
            long previousToGo = peers.getToGo();
            // 根据offset计算差值
            long incrementUploaded;
            long incrementDownloaded;
            if (peers.getDownloadOffset() > request.downloaded() || peers.getUploadOffset() > request.uploaded()) {
                // 客户端数据重置了，下载器重启过？那么本次提交数据全部加上
                incrementUploaded = request.uploaded();
                incrementDownloaded = request.downloaded();
            } else {
                // 正常情况下，只有增加的数据
                incrementUploaded = request.uploaded() - peers.getUploadOffset();
                incrementDownloaded = request.downloaded() - peers.getDownloadOffset();
            }

            var redisDupeCheckKey = "dupe_announce_check:torrent" + request.torrentId() + ":user:" + request.userId() + ":peer:" + HexFormat.of().formatHex(request.peerId());
            var stored = redisTemplate.opsForValue().get(redisDupeCheckKey);
            if (stored != null && System.currentTimeMillis() < (Long.parseLong(stored) + 30000)) {
                // 避免重复计算
                incrementUploaded = 0;
                incrementDownloaded = 0;
            } else {
                redisTemplate.opsForValue().set(redisDupeCheckKey, String.valueOf(System.currentTimeMillis()), 30000, TimeUnit.MILLISECONDS);
            }

            // 更新到当前状态
            peers.setUploadOffset(request.uploaded());
            peers.setDownloadOffset(request.downloaded());
            peers.setUploaded(peers.getUploaded() + incrementUploaded);
            peers.setDownloaded(peers.getDownloaded() + incrementDownloaded);
            peers.setPort(request.port());
            peers.setToGo(request.left());
            peers.setLastAnnounce(now);
            peers.setLastAction(request.peerEvent().toString());
            peers.setConnectable(null);
            peers.setUserAgent(request.ua());
            peers.setReqIp(request.reqIpInetAddress());

            if (request.peerEvent() != PeerEvent.STOPPED) {
                saveOrUpdate(peers);
            } else {
                if (peers.getId() != null) {
                    removeById(peers);
                }
            }
            // ---------------------------
            // 计算下载/做种时间
            boolean previousSeeding = previousEvent == PeerEvent.COMPLETED || previousToGo == 0;
            boolean currentSeeding = request.peerEvent() == PeerEvent.COMPLETED || request.left() == 0;
            long incrementSeedingTime = 0;
            long incrementLeechingTime = 0;
            long duration = (now.toEpochSecond() - previousAnnounce.toEpochSecond()) * 1000;
            if (previousSeeding && currentSeeding) {
                // 获取上次宣告到当前的毫秒数
                incrementSeedingTime = duration;
            } else {
                incrementLeechingTime = duration;
            }
            userTaskRecordsService.updateUserTaskRecord(
                    request.userId(),
                    request.torrentId(),
                    request.left(),
                    now,
                    request.peerEvent(),
                    incrementUploaded,
                    incrementDownloaded,
                    incrementSeedingTime,
                    incrementLeechingTime,
                    request.ua()
            );
            usersService.updateUsersStatisticalData(request.userId(), incrementUploaded, incrementDownloaded, incrementSeedingTime, incrementLeechingTime);
            log.info("User {} announce {} event {} left {} incrementUpload {} incrementDownload {} ip {}",
                    request.userId(), request.torrentId(), request.peerEvent(), request.left(), incrementUploaded, incrementDownloaded, request.peerIp().getHostAddress());
        }
    }
}
