package com.ghostchu.tracker.sapling.mapper;

import com.ghostchu.tracker.sapling.entity.Peers;

import java.net.InetAddress;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface PeersMapper extends SaplingMapper<Peers> {
    Peers selectPeersForUpdate(long torrent, long owner, byte[] peerId, InetAddress ip);
}

