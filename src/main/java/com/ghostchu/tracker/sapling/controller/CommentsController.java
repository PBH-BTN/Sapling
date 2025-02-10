package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.ghostchu.tracker.sapling.entity.Comments;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.ICommentsService;
import com.ghostchu.tracker.sapling.util.HtmlSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Controller
@RequestMapping("/comments")
public class CommentsController {
    @Autowired
    private ICommentsService commentsService;

    @PostMapping
    @ResponseBody
    @SaCheckPermission(Permission.COMMENT_CREATE)
    public ResponseEntity<?> createComment(
            @RequestParam Long torrentId,
            @RequestParam(required = false) Long parentId,
            @RequestParam String content) {
        // 验证父评论是否属于同一个种子
        if (parentId != null) {
            Comments parent = commentsService.getCommentById(parentId);
            if (parent == null || parent.getTorrent() != torrentId) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "父评论不存在或不属于当前资源"));
            }
        }
        Comments comment = commentsService.createComment(StpUtil.getLoginIdAsLong(), torrentId, parentId, HtmlSanitizer.sanitize(content));
        return ResponseEntity.ok(commentsService.toVO(comment, false));
    }

    // 删除评论（新增）
    @DeleteMapping("/{commentId}")
    @SaCheckPermission(value = {Permission.COMMENT_DELETE, Permission.COMMENT_DELETE_OTHER}, mode = SaMode.OR)
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        // 权限校验
        Comments comment = commentsService.getCommentById(commentId);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }
        if (comment.getOwner() == StpUtil.getLoginIdAsLong()) {
            StpUtil.checkPermission(Permission.COMMENT_DELETE);
        } else {
            StpUtil.checkPermission(Permission.COMMENT_DELETE_OTHER);
        }
        comment.setDeletedBy(StpUtil.getLoginIdAsLong());
        comment.setDeletedAt(OffsetDateTime.now());
        commentsService.updateById(comment);
        return ResponseEntity.ok().build();
    }
}
