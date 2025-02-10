package com.ghostchu.tracker.sapling.controller.admin.users;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.vo.UserVO;
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
        IPage<UserVO> userVOPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal(), userPage.searchCount());
        userVOPage.setRecords(userPage.getRecords().stream().map(usersService::toVO).toList());
        model.addAttribute("users", userVOPage);
        model.addAttribute("search", search);
        return "admin/users/list";
    }
}
