package com.ghostchu.tracker.sapling.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.TorrentTags;
import com.ghostchu.tracker.sapling.vo.TorrentTagsVO;
import com.github.yulichang.base.MPJBaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface ITorrentTagsService extends MPJBaseService<TorrentTags> {

    List<TorrentTags> getTorrentTags(long torrentId);

    TorrentTagsVO toVO(TorrentTags torrentTags);

    IPage<TorrentTags> getTorrentTagsByPage(long torrentId, long page, int size);

    TorrentTags addTag(long torrentId, long tagId);

    boolean removeTag(long torrentId, long tagId);

    String createTagString(List<TorrentTags> torrentTags);

    @Transactional
    void applyTagString(long torrent, String tagString, boolean createNewTag);

    void removeSpecificTagsFromAllTorrents(Long id);
}
