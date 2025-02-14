package com.ghostchu.tracker.sapling.controller.admin.users;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.IUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/users/list")
@SaCheckPermission(Permission.ADMIN_USERS_LIST)
@SaCheckDisable
public class AdminUsersListController {
    @Autowired
    private IUsersService usersService;

    @GetMapping
    public String userList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {
        IPage<Users> userPage = usersService.searchUsers(page, size, search);
        model.addAttribute("users", userPage.convert(usersService::toVO).getRecords());
        model.addAttribute("search", search);
        return "admin/users/list";
    }
}
