package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.TorrentTags;
import com.ghostchu.tracker.sapling.mapper.TorrentTagsMapper;
import com.ghostchu.tracker.sapling.service.ITagsService;
import com.ghostchu.tracker.sapling.service.ITorrentTagsService;
import com.ghostchu.tracker.sapling.service.ITorrentsService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.vo.TorrentTagsVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

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
public class TorrentTagsServiceImpl extends MPJBaseServiceImpl<TorrentTagsMapper, TorrentTags> implements ITorrentTagsService {
    @Autowired
    private ITorrentsService torrentsService;
    @Autowired
    private ITagsService tagsService;
    @Autowired
    private IUsersService usersService;

    @Override
    @Cacheable(value = "torrentTags", key = "'torrentId:' + #torrentId")
    public List<TorrentTags> getTorrentTags(long torrentId) {
        return list(new QueryWrapper<TorrentTags>().eq("torrent", torrentId));
    }

    @Override
    public TorrentTagsVO toVO(TorrentTags torrentTags) {
        var torrents = torrentsService.getTorrentById(torrentTags.getTorrent());
        var tags = tagsService.getTags(torrentTags.getTag());
        if (tags == null || torrents == null) return null;
        TorrentTagsVO torrentTagsVO = new TorrentTagsVO();
        torrentTagsVO.setId(torrentTags.getId());
        torrentTagsVO.setTorrent(torrentsService.toVO(torrents));
        torrentTagsVO.setTag(tagsService.toVO(tags));
        torrentTagsVO.setExtra(torrentTags.getExtra());
        return torrentTagsVO;
    }

    @Override
    public IPage<TorrentTags> getTorrentTagsByPage(long torrentId, long page, int size) {
        return page(new Page<>(page, size), new QueryWrapper<TorrentTags>().eq("torrent", torrentId));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "torrentTags", key = "'torrentId:' + #torrentId")
    })
    public TorrentTags addTag(long torrentId, long tagId) {
        TorrentTags torrentTags = new TorrentTags();
        torrentTags.setTorrent(torrentId);
        torrentTags.setTag(tagId);
        if (!save(torrentTags)) {
            throw new IllegalStateException("标签添加失败，是否已存在相同标签？");
        }
        return torrentTags;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "torrentTags", key = "'torrentId:' + #torrentId")
    })
    public boolean removeTag(long torrentId, long tagId) {
        return remove(new QueryWrapper<TorrentTags>().eq("torrent", torrentId).eq("tag", tagId));
    }

}
