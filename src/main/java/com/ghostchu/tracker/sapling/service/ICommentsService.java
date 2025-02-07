package com.ghostchu.tracker.sapling.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.Comments;
import com.ghostchu.tracker.sapling.vo.CommentsVO;
import com.github.yulichang.base.MPJBaseService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface ICommentsService extends MPJBaseService<Comments> {

    IPage<Comments> getComments(long torrent, int page, int size);

    Comments createComment(long loginIdAsLong, long torrentId, Long parentId, String content);

    CommentsVO toVO(Comments comments, boolean reference);

    Comments getCommentById(Long commentId);

    void removeCommentById(Long commentId, long loginIdAsLong);
}
