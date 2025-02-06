package com.ghostchu.tracker.sapling.controller.advice;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.stp.StpUtil;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.exception.BusinessException;
import com.ghostchu.tracker.sapling.service.IUsersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@Slf4j
public class SaplingControllerAdvice {
    @Autowired
    private IUsersService usersService;

    @ExceptionHandler(NotLoginException.class)
    public String handlerNotLoginException(NotLoginException e, Model model) {
        model.addAttribute("err", "您还未登录，请先登录！");
        return "error";
    }

    @ExceptionHandler(NotPermissionException.class)
    public String handlerNotLoginException(NotPermissionException e, Model model) {
        model.addAttribute("err", "您没有足够的权限访问此模块的内容：" + e.getMessage());
        return "error";
    }

    // 业务异常拦截
    @ExceptionHandler
    public String handlerException(BusinessException e, Model model) {
        model.addAttribute("err", e.getMessage());
        return "error";
    }

    // 全局异常拦截
    @ExceptionHandler
    public String handlerException(Exception e, Model model) {
        model.addAttribute("err", e.getMessage());
        log.error("未处理的全局异常: {}", e.getMessage(), e);
        return "error";
    }


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
