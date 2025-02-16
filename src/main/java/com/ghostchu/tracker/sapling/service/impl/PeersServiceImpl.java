package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.Peers;
import com.ghostchu.tracker.sapling.entity.Promotions;
import com.ghostchu.tracker.sapling.entity.Settings;
import com.ghostchu.tracker.sapling.entity.Torrents;
import com.ghostchu.tracker.sapling.entity.projection.PeerStats;
import com.ghostchu.tracker.sapling.gvar.Setting;
import com.ghostchu.tracker.sapling.mapper.PeersMapper;
import com.ghostchu.tracker.sapling.model.AnnounceRequest;
import com.ghostchu.tracker.sapling.model.ScrapePeers;
import com.ghostchu.tracker.sapling.service.*;
import com.ghostchu.tracker.sapling.tracker.PeerEvent;
import com.ghostchu.tracker.sapling.vo.PeersVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Slf4j
@Service
public class PeersServiceImpl extends MPJBaseServiceImpl<PeersMapper, Peers> implements IPeersService {
    @Autowired
    private IUserTasksService userTaskRecordsService;
    @Autowired
    private IUsersService usersService;
    @Autowired
    private ISettingsService settingsService;
    @Autowired
    private IUserStatsService userStatsService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ITorrentsService torrentsService;
    @Autowired
    private IPromotionsService promotionsService;

    @Override
    public IPage<Peers> fetchPeers(long userId, long torrentId, int limit, boolean random, @Nullable Integer specificIpProtocolVersion) {
        IPage<Peers> peersIPage = new Page<>(1, limit);
        var wrapper = new QueryWrapper<Peers>()
                .eq("torrent", torrentId)
                .orderByAsc(random, "RANDOM()")
                .orderByDesc(!random, "last_announce");
        if (specificIpProtocolVersion != null) {
            wrapper = wrapper.eq("family(ip) = {0}", specificIpProtocolVersion);
        }
        wrapper = wrapper.ne("owner", userId);
        return page(peersIPage, wrapper);
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
            long realIncreamentUploaded;
            long realIncreamentDownloaded;
            if (peers.getDownloadOffset() > request.downloaded() || peers.getUploadOffset() > request.uploaded()) {
                // 客户端数据重置了，下载器重启过？那么本次提交数据全部加上
                realIncreamentUploaded = request.uploaded();
                realIncreamentDownloaded = request.downloaded();
            } else {
                // 正常情况下，只有增加的数据
                realIncreamentUploaded = request.uploaded() - peers.getUploadOffset();
                realIncreamentDownloaded = request.downloaded() - peers.getDownloadOffset();
            }

            var redisKey = "announce_dupe_check:" + request.userId() + ":" + request.torrentId() + ":" + HexFormat.of().formatHex(request.peerId());
            var currentValue = "E:" + request.peerEvent().name() + "U:" + request.uploaded() + ",D:" + request.downloaded();
            String cachedValue = redisTemplate.opsForValue().getAndSet(redisKey, currentValue);
            redisTemplate.expire(redisKey, 60000, TimeUnit.MILLISECONDS);
            if (currentValue.equals(cachedValue)) {
                realIncreamentUploaded = 0;
                realIncreamentDownloaded = 0;
            }

            long incrementUploaded = realIncreamentUploaded;
            long incrementDownloaded = realIncreamentDownloaded;

            Torrents torrents = torrentsService.getTorrentById(request.torrentId());
            Promotions promotions = promotionsService.getPromotionsByIdAndPromotionStatus(torrents.getPromotion(), torrents.getPromotionUntil());

            if (promotions != null) {
                incrementUploaded = (long) (realIncreamentUploaded * promotions.getUploadModifier());
                incrementDownloaded = (long) (realIncreamentDownloaded * promotions.getDownloadModifier());
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
                    realIncreamentUploaded,
                    realIncreamentDownloaded,
                    incrementSeedingTime,
                    incrementLeechingTime,
                    request.ua()
            );
            var userStats = userStatsService.selectUserStatsForUpdate(request.userId());
            userStats.setUploaded(userStats.getUploaded() + incrementUploaded);
            userStats.setDownloaded(userStats.getDownloaded() + incrementDownloaded);
            userStats.setUploadedReal(userStats.getUploadedReal() + realIncreamentUploaded);
            userStats.setDownloadedReal(userStats.getDownloadedReal() + realIncreamentDownloaded);
            if (request.left() == 0 && previousToGo == 0) {
                userStats.setSeedTime(userStats.getSeedTime() + incrementSeedingTime);
            } else {
                userStats.setLeechTime(userStats.getLeechTime() + incrementLeechingTime);
            }
            userStatsService.updateUserStats(userStats);
            log.info("User {} announce {} event {} left {} incrementUpload {} incrementDownload {} ip {}",
                    request.userId(), request.torrentId(), request.peerEvent(), request.left(), realIncreamentUploaded, realIncreamentDownloaded, request.peerIp().getHostAddress());
        }
    }

    @Override
    @Cacheable(value = "peers_count", key = "'torrent:' + #torrentId")
    public PeerStats countPeersByTorrent(@Param("torrent") Long torrentId) {
        return baseMapper.countPeersByTorrent(torrentId);
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void cleanupPeers() {
        Settings interval = settingsService.getSetting(Setting.TRACKER_ANNOUNCE_INTERVAL);
        Settings randomOffset = settingsService.getSetting(Setting.TRACKER_ANNOUNCE_INTERVAL_RANDOM_OFFSET);
        long intervalValue = Long.parseLong(interval.getValue()) + Long.parseLong(randomOffset.getValue()) + 30000;
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime cutoff = now.minus(intervalValue, ChronoUnit.MILLIS);
        int removed = this.baseMapper.delete(new QueryWrapper<Peers>().le("last_announce", cutoff));
        log.info("Cleanup peers, removed {} peers", removed);
    }

    @Override
    public PeersVO toVO(Peers peers) {
        PeersVO peersVO = new PeersVO();
        peersVO.setId(peers.getId() == null ? 0 : peers.getId());
        peersVO.setTorrent(torrentsService.toVO(torrentsService.getTorrentById(peers.getTorrent())));
        peersVO.setOwner(usersService.toVO(usersService.getUserById(peers.getOwner())));
        peersVO.setPeerId(peers.getPeerId());
        peersVO.setIp(peers.getIp());
        peersVO.setPort(peers.getPort());
        peersVO.setUploaded(peers.getUploaded());
        peersVO.setDownloaded(peers.getDownloaded());
        peersVO.setToGo(peers.getToGo());
        peersVO.setStarted(peers.getStarted());
        peersVO.setLastAnnounce(peers.getLastAnnounce());
        peersVO.setConnectable(peers.getConnectable());
        peersVO.setDownloadOffset(peers.getDownloadOffset());
        peersVO.setUploadOffset(peers.getUploadOffset());
        peersVO.setUserAgent(peers.getUserAgent());
        peersVO.setLastAction(peers.getLastAction());
        peersVO.setReqIp(peers.getReqIp());
        return peersVO;
    }
}
