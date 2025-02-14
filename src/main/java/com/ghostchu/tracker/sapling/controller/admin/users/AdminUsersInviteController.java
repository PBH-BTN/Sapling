package com.ghostchu.tracker.sapling.controller.admin.users;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.Invites;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.IInvitesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/users/invites")
@SaCheckPermission(Permission.ADMIN_USERS_INVITES)
@SaCheckDisable
public class AdminUsersInviteController {
    @Autowired
    private IInvitesService invitesService;

    @GetMapping()
    public String inviteList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String search,
            Model model) {
        IPage<Invites> invitePage = invitesService.listInvites(page, size, search);
        model.addAttribute("invites", invitePage.convert(invitesService::toVO));
        return "admin/users/invites";
    }
}