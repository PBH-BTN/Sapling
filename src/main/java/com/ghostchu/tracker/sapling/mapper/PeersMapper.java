package com.ghostchu.tracker.sapling.mapper;

import com.ghostchu.tracker.sapling.entity.Peers;
import org.apache.ibatis.annotations.Param;

import java.net.InetAddress;
import java.util.List;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface PeersMapper extends SaplingMapper<Peers> {
    Peers selectPeersForUpdateByIp(@Param("torrent") long torrent,
                                   @Param("owner") long owner,
                                   @Param("peerId") byte[] peerId,
                                   @Param("ip") InetAddress ip);

    List<Peers> selectPeersForUpdateList(@Param("torrent") long torrent,
                                         @Param("owner") long owner,
                                         @Param("peerId") byte[] peerId);
}

