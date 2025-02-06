package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.ghostchu.tracker.sapling.entity.Comments;
import com.ghostchu.tracker.sapling.mapper.CommentsMapper;
import com.ghostchu.tracker.sapling.service.ICommentsService;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class CommentsServiceImpl extends MPJBaseServiceImpl<CommentsMapper, Comments> implements ICommentsService {
    @Override
    public PageDTO<Comments> getComments(long torrent, int page, int size) {
        return baseMapper.selectPage(new PageDTO<>(page, size), new QueryWrapper<Comments>().eq("torrent", torrent));
    }
}
