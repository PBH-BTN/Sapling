package com.ghostchu.tracker.sapling.controller.admin.users;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.Invites;
import com.ghostchu.tracker.sapling.service.IInvitesService;
import com.ghostchu.tracker.sapling.vo.InvitesVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/users/invites")
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
        IPage<InvitesVO> invitesVOIPage = new Page<>(invitePage.getCurrent(), invitePage.getSize(), invitePage.getTotal(), invitePage.searchCount());
        invitesVOIPage.setRecords(invitePage.getRecords().stream().map(invitesService::toVO).toList());
        model.addAttribute("invites", invitesVOIPage);
        return "admin/users/invites";
    }
}