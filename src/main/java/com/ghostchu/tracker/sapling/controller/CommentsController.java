package com.ghostchu.tracker.sapling.controller;

import com.ghostchu.tracker.sapling.service.ICommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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

//    @PostMapping
//    public String createComment(
//            @RequestParam Long torrentId,
//            @RequestParam(required = false) Long parentId,
//            @RequestParam String content) {
//
//        // 创建评论逻辑
//        commentService.createComment(torrentId, parentId, content, user);
//        return "redirect:/torrents/" + torrentId;
//    }
//
//    @DeleteMapping("/{id}")
//    public String deleteComment(
//            @PathVariable Long id,) {
//
//        // 删除评论逻辑
//        Long torrentId = commentService.deleteComment(id, user);
//        return "redirect:/torrents/" + torrentId;
//    }

}
