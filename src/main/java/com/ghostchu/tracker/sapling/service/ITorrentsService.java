package com.ghostchu.tracker.sapling.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.Torrents;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.vo.TorrentDetailsVO;
import com.ghostchu.tracker.sapling.vo.TorrentVO;
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
    Torrents getTorrentById(Long id);

    IPage<Torrents> getTorrentsByPage(long page, int size, String keyword, boolean includeInvisible, boolean includeDeleted);


    TorrentVO toVO(Torrents torrent);

    TorrentDetailsVO toDetailsVO(Torrents torrent);

    boolean isTorrentExists(byte[] infoHash);

    byte[] downloadTorrentForUser(Torrents torrents, Users user) throws IOException;

    Torrents createTorrent(Long owner, MultipartFile file, Long categoryId, String title, String subtitle, String description, boolean anonymous, boolean visible) throws IOException;

    Torrents updateTorrent(Long id, Long userId, Long categoryId, String title, String subtitle, String description);

    Torrents getTorrentByInfoHash(byte[] infoHash);

    Torrents deleteTorrent(long id, long deleteBy);

    Torrents unDeleteTorrent(long id);
}
