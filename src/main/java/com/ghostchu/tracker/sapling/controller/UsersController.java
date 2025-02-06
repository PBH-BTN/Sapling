package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.exception.UserNotExistsException;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private IUsersService userService;

    // 查看当前用户资料
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        UserVO currentUser = userService.toVO(userService.getUserById(StpUtil.getLoginIdAsLong()));
        model.addAttribute("user", currentUser);
        model.addAttribute("isCurrentUser", true);
        return "users/profile";
    }

    // 查看其他用户资料
    @GetMapping("/profile/{userId}")
    public String viewUserProfile(
            @PathVariable long userId,
            Model model) {
        Users users = userService.getUserById(userId);
        if (users == null) {
            throw new UserNotExistsException(userId, "指定的用户不存在");
        }
        UserVO targetUser = userService.toVO(userService.getUserById(userId));

        boolean isCurrentUser = targetUser.getId() == StpUtil.getLoginIdAsLong();
        model.addAttribute("user", targetUser);
        model.addAttribute("isCurrentUser", isCurrentUser);
        return "users/profile";
    }
}
