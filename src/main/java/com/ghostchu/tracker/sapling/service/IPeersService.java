package com.ghostchu.tracker.sapling.service;

import com.ghostchu.tracker.sapling.entity.Peers;
import com.ghostchu.tracker.sapling.entity.projection.PeerStats;
import com.ghostchu.tracker.sapling.model.AnnounceRequest;
import com.ghostchu.tracker.sapling.model.ScrapePeers;
import com.github.yulichang.base.MPJBaseService;
import jakarta.annotation.Nullable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface IPeersService extends MPJBaseService<Peers> {

    List<Peers> fetchPeers(long userId, long torrentId, int limit, @Nullable Integer specificIpProtocolVersion);

    ScrapePeers scrape(long torrentId);

    void announce(List<AnnounceRequest> requests);

    PeerStats countPeersByTorrent(@Param("torrent") Long torrentId);
}
