package com.ghostchu.tracker.sapling.controller.admin;

import cn.dev33.satoken.annotation.SaCheckDisable;
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
@SaCheckDisable
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

        PrimaryMenuVO userMenu = new PrimaryMenuVO(1L, "用户管理", null);
        userMenu.setSubMenus(Arrays.asList(
                new SubMenuVO(101L, "用户列表", "/admin/users/list"),
                new SubMenuVO(102L, "考核中用户", "/admin/users/exam"),
                new SubMenuVO(103L, "H/R 状态", "/admin/users/hitandrun"),
                new SubMenuVO(104L, "用户邀请", "/admin/users/invites")
        ));

        PrimaryMenuVO torrentMenu = new PrimaryMenuVO(2L, "种子管理", null);
        torrentMenu.setSubMenus(Arrays.asList(
                new SubMenuVO(202L, "种子促销", "/admin/torrents/promotions"),
                new SubMenuVO(203L, "分类管理", "/admin/torrents/categories"),
                new SubMenuVO(204L, "标签管理", "/admin/torrents/tags")

        ));

        PrimaryMenuVO trackerMenu = new PrimaryMenuVO(3L, "Tracker", null);
        trackerMenu.setSubMenus(Arrays.asList(
                new SubMenuVO(301L, "Tracker 服务器", "/admin/tracker/settings"),
                new SubMenuVO(302L, "客户端许可", "/admin/tracker/clients")
        ));

        PrimaryMenuVO siteMenu = new PrimaryMenuVO(4L, "站点管理", null);
        siteMenu.setSubMenus(Arrays.asList(
                new SubMenuVO(401L, "站点公告", "/admin/site/news"),
                new SubMenuVO(402L, "静态页面", "/admin/site/pages"),
                new SubMenuVO(403L, "文件仓库", "/admin/site/bitbucket"),
                new SubMenuVO(404L, "积分货币", "/admin/site/currencies"),
                new SubMenuVO(405L, "道具管理", "/admin/site/widgets")
        ));

        PrimaryMenuVO permissionMenu = new PrimaryMenuVO(5L, "权限管理", null);
        permissionMenu.setSubMenus(Arrays.asList(
                new SubMenuVO(501L, "主用户组", "/admin/permissions/primaryPermissionGroups"),
                new SubMenuVO(502L, "晋级用户组", "/admin/permissions/levelPermissionGroups"),
                new SubMenuVO(503L, "权限节点分配", "/admin/permissions/permissionNodes")
        ));

        menus.add(siteMenu);
        menus.add(userMenu);
        menus.add(torrentMenu);
        menus.add(trackerMenu);
        menus.add(permissionMenu);
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
