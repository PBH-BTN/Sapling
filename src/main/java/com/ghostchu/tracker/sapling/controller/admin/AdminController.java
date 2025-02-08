package com.ghostchu.tracker.sapling.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.vo.PrimaryMenuVO;
import com.ghostchu.tracker.sapling.vo.SubMenuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/admin")
@Controller
@SaCheckPermission(Permission.ADMIN_DASHBOARD)
public class AdminController {
    @Autowired
    private ObjectMapper mapper;

    @GetMapping
    public String adminDashboard(Model model) {
        List<PrimaryMenuVO> menuMap = buildAdminMenu();
        model.addAttribute("menuMap", menuMap);
        model.addAttribute("menuMapJson", serializeMenu(menuMap));
        return "admin/index";
    }

    private List<PrimaryMenuVO> buildAdminMenu() {
        List<PrimaryMenuVO> menus = new ArrayList<>();

        // 系统设置菜单
        PrimaryMenuVO userMenu = new PrimaryMenuVO(1L, "用户管理", null);
        userMenu.setSubMenus(Arrays.asList(
                new SubMenuVO(101L, "用户列表", "/admin/users/list"),
                new SubMenuVO(102L, "考核中用户", "/admin/users/exam"),
                new SubMenuVO(103L, "H/R 状态", "/admin/users/hitandrun"),
                new SubMenuVO(104L, "用户邀请", "/admin/users/invites")
        ));

        PrimaryMenuVO torrentMenu = new PrimaryMenuVO(2L, "种子管理", null);
        torrentMenu.setSubMenus(Arrays.asList(
                new SubMenuVO(201L, "种子列表", "/admin/torrents/list"),
                new SubMenuVO(202L, "种子促销", "/admin/torrents/promotion"),
                new SubMenuVO(203L, "标签管理", "/admin/torrents/tags")

        ));

        // 用户管理菜单
        PrimaryMenuVO trackerMenu = new PrimaryMenuVO(3L, "Tracker", null);
        trackerMenu.setSubMenus(Arrays.asList(
                new SubMenuVO(301L, "客户端许可", "/admin/tracker/clients"),
                new SubMenuVO(302L, "H/R", "/admin/tracker/hitandrun"),
                new SubMenuVO(303L, "Seedbox", "/admin/tracker/seedbox")
        ));

        menus.add(userMenu);
        menus.add(torrentMenu);
        menus.add(trackerMenu);
        return menus;
    }

    private String serializeMenu(List<PrimaryMenuVO> menus) {
        try {
            return mapper.writeValueAsString(menus);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
