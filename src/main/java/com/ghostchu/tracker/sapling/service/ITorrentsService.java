package com.ghostchu.tracker.sapling.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.dto.TorrentDTO;
import com.ghostchu.tracker.sapling.entity.Torrents;
import com.github.yulichang.base.MPJBaseService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface ITorrentsService extends MPJBaseService<Torrents> {

    IPage<Torrents> getTorrentsByPage(long page, int size, boolean includeInvisible, boolean includeDeleted);

    TorrentDTO toDTO(Torrents torrent);

    Torrents createTorrent(Long owner, MultipartFile file, Long categoryId, String title, String subtitle, String description, boolean anonymous) throws IOException;
}
