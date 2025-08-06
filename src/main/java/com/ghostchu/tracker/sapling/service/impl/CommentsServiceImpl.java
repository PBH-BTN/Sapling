package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.ghostchu.tracker.sapling.entity.Comments;
import com.ghostchu.tracker.sapling.mapper.CommentsMapper;
import com.ghostchu.tracker.sapling.service.ICommentsService;
import com.ghostchu.tracker.sapling.service.ITorrentsService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.vo.CommentsVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

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
    @Autowired
    private IUsersService usersService;
    @Autowired
    private ITorrentsService torrentsService;

    @Override
    public PageDTO<Comments> getComments(long torrent, int page, int size) {
        return baseMapper.selectPage(new PageDTO<>(page, size), new QueryWrapper<Comments>().eq("torrent", torrent));
    }

    @Override
    @CacheEvict(value = "torrent_comment_count", key = "#torrentId")
    public Comments createComment(long loginIdAsLong, long torrentId, Long parentId, String content) {
        Comments comment = new Comments();
        comment.setOwner(loginIdAsLong);
        comment.setTorrent(torrentId);
        comment.setParentComment(parentId);
        comment.setContent(content);
        comment.setCreatedAt(OffsetDateTime.now());
        baseMapper.insert(comment);
        return comment;
    }

    @Override
    public CommentsVO toVO(Comments comments, boolean reference) {
        CommentsVO vo = new CommentsVO();
        vo.setId(comments.getId() == null ? 0 : comments.getId());
        vo.setOwner(usersService.toVO(usersService.getUserById(comments.getOwner())));
        vo.setTorrent(torrentsService.toVO(torrentsService.getTorrentById(comments.getTorrent())));
        vo.setContent(comments.getContent());
        vo.setParentComment(reference ? toVO(this.baseMapper.selectById(comments.getParentComment()), true) : null);
        vo.setCreatedAt(comments.getCreatedAt());
        vo.setEditedAt(comments.getEditedAt());
        vo.setDeletedAt(comments.getDeletedAt());
        return vo;
    }

    @Override
    public Comments getCommentById(Long commentId) {
        return baseMapper.selectById(commentId);
    }

    @Override
    @CacheEvict(value = "torrent_comment_count", key = "#torrentId")
    public void removeCommentById(Long commentId, long loginIdAsLong) {
        baseMapper.deleteById(commentId);
    }

    @Override
    @Cacheable(value = "torrent_comment_count", key = "#torrentId")
    public long getCommentsCount(Long torrentId) {
        return count(new QueryWrapper<Comments>().eq("torrent", torrentId));
    }
}
