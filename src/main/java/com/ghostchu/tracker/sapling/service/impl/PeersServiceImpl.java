package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ghostchu.tracker.sapling.entity.Peers;
import com.ghostchu.tracker.sapling.mapper.PeersMapper;
import com.ghostchu.tracker.sapling.model.AnnounceRequest;
import com.ghostchu.tracker.sapling.model.ScrapePeers;
import com.ghostchu.tracker.sapling.service.IPeersService;
import com.ghostchu.tracker.sapling.tracker.PeerEvent;
import com.github.yulichang.base.MPJBaseServiceImpl;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class PeersServiceImpl extends MPJBaseServiceImpl<PeersMapper, Peers> implements IPeersService {
    @Override
    public List<Peers> fetchPeers(long userId, long torrentId, int limit, @Nullable Integer specificIpProtocolVersion) {
        var wrapper = new QueryWrapper<Peers>()
                .eq("torrent", torrentId)
                .ne("left", 0)
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
        for (AnnounceRequest request : requests) {
            if (request.peerEvent() == PeerEvent.STOPPED) {
                remove(new QueryWrapper<Peers>()
                        .eq("torrent", request.torrentId())
                        .eq("owner", request.userId())
                        .eq("ip", request.peerIp())
                        .eq("peer_id", request.peerId())
                );
                continue;
            }
            Peers peers = getOne(new QueryWrapper<Peers>()
                    .eq("torrent", request.torrentId())
                    .eq("owner", request.userId())
                    .eq("ip", request.peerIp())
                    .eq("peer_id", request.peerId())
            );
            if (peers == null) {
                peers = new Peers();
                peers.setTorrent(request.torrentId());
                peers.setPeerId(request.peerId());
                peers.setIp(request.peerIp());
                peers.setStarted(OffsetDateTime.now());
            }
            peers.setPort(request.port());
            peers.setToGo(request.left());
            peers.setLastAnnounce(OffsetDateTime.now());
            peers.setConnectable(null);
            peers.setUserAgent(request.ua());
            peers.setLastAction(request.peerEvent().toString());
            // 根据offset计算差值
            if (request.downloaded() < peers.getDownloadOffset() || request.uploaded() < peers.getUploadOffset()) {
                // 客户端数据重置了，下载器重启过？那么本次提交数据全部加上
                peers.setUploaded(peers.getUploaded() + request.uploaded());
                peers.setDownloaded(peers.getDownloaded() + request.downloaded());
            } else {
                // 正常情况下，只有增加的数据
                peers.setUploaded(peers.getUploadOffset() - peers.getUploaded());
                peers.setDownloaded(peers.getDownloadOffset() - peers.getDownloaded());
            }
            // 更新offset
            peers.setUploadOffset(request.uploaded());
            peers.setDownloadOffset(request.downloaded());
            saveOrUpdate(peers);
        }
    }
}
