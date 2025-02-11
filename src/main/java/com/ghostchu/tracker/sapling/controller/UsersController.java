package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.ghostchu.tracker.sapling.dto.ConfirmFormDTO;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.exception.UserNotExistsException;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.google.common.html.HtmlEscapers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

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
@SaCheckDisable
public class UsersController {
    @Autowired
    private IUsersService userService;

    @GetMapping("/{id}/ban")
    @SaCheckPermission(value = {Permission.USER_BAN})
    public String banUser(Model model, @PathVariable long id) {
        Users users = userService.getUserById(id);
        if (users == null) {
            throw new UserNotExistsException(id, "指定的用户不存在");
        }
        model.addAttribute("userToBan", userService.toVO(users));
        return "users/ban";
    }

    @GetMapping("/{id}/unban")
    @SaCheckPermission(value = {Permission.USER_UNBAN})
    public String unbanUser(Model model, @PathVariable long id) {
        Users users = userService.getUserById(id);
        if (users == null) {
            throw new UserNotExistsException(id, "指定的用户不存在");
        }
        ConfirmFormDTO form = new ConfirmFormDTO();
        form.setActionUrl("/users/" + id + "/unban");
        form.setTitle("解除封禁");
        form.setHeaderClass("bg-warning text-white");
        form.setDescription("您确定要解除对用户 " + HtmlEscapers.htmlEscaper().escape(users.getName()) + " 的封禁吗？");
        form.setConfirmButtonText("解除");
        model.addAttribute("form", form);
        model.addAttribute("userToBan", userService.toVO(users));
        return "confirm";
    }

    // 处理封禁请求
    @PostMapping("/{id}/ban")
    @SaCheckPermission(value = {Permission.USER_BAN})
    public String processBanUser(
            @PathVariable long id,
            @RequestParam String reason) {
        Users users = userService.getUserById(id);
        if (users == null) {
            throw new UserNotExistsException(id, "指定的用户不存在");
        }
        userService.banUser(id, StpUtil.getLoginIdAsLong(), reason, OffsetDateTime.now().plusYears(100));
        return "redirect:/users/profile/" + id;
    }

    // 处理封禁请求
    @PostMapping("/{id}/unban")
    @SaCheckPermission(value = {Permission.USER_BAN})
    public String processUnbanUser(@PathVariable long id) {
        Users users = userService.getUserById(id);
        if (users == null) {
            throw new UserNotExistsException(id, "指定的用户不存在");
        }
        userService.unbanUser(id, StpUtil.getLoginIdAsLong());
        return "redirect:/users/profile/" + id;
    }




}
