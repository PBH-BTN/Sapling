package com.ghostchu.tracker.sapling.controller.advice;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.service.IUsersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SaplingControllerAdvice {
    @Autowired
    private IUsersService usersService;

    @ModelAttribute("user")
    public Users addUserToModel(HttpServletRequest request) {
        if (!StpUtil.isLogin()) return null;
        try {
            return usersService.getById(StpUtil.getLoginIdAsLong());
        } catch (NotLoginException e) {
            return null;
        }
    }
}
