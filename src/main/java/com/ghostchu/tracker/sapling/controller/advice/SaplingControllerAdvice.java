package com.ghostchu.tracker.sapling.controller.advice;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.stp.StpUtil;
import com.ghostchu.tracker.sapling.entity.UserBans;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.exception.BusinessException;
import com.ghostchu.tracker.sapling.gvar.Setting;
import com.ghostchu.tracker.sapling.service.ISettingsService;
import com.ghostchu.tracker.sapling.service.IUserBansService;
import com.ghostchu.tracker.sapling.service.IUserStatsService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.vo.UserStatsVO;
import com.ghostchu.tracker.sapling.vo.UserVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class SaplingControllerAdvice {
    @Autowired
    private IUsersService usersService;
    @Autowired
    private ISettingsService settingsService;
    @Autowired
    private IUserBansService userBansService;
    @Autowired
    private IUserStatsService userStatsService;

    @ExceptionHandler(NotLoginException.class)
    public String handlerNotLoginException(NotLoginException e, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        addModelAttributesForExceptionHandler(model);
        model.addAttribute("err", "您还未登录，请先登录！");
        return "error";
    }

    @ExceptionHandler(NotPermissionException.class)
    public String handlerNotLoginException(NotPermissionException e, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        addModelAttributesForExceptionHandler(model);
        model.addAttribute("err", "您没有足够的权限访问此模块的内容：" + e.getMessage());
        return "error";
    }

    @ExceptionHandler(DisableServiceException.class)
    public String handlerDisableServiceException(DisableServiceException e, Model model, HttpServletResponse response) {
        Users users = usersService.getUserById(StpUtil.getLoginIdAsLong());
        UserBans userBans = userBansService.getBanRecord(users.getBannedId());
        model.addAttribute("userBan", usersService.toUserBanVO(userBans));
        addModelAttributesForExceptionHandler(model);
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return "error_banned";
    }

    // 业务异常拦截
    @ExceptionHandler(BusinessException.class)
    public String handlerException(BusinessException e, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        addModelAttributesForExceptionHandler(model);
        model.addAttribute("err", e.getMessage());
        return "error";
    }

    // 业务异常拦截
    @ExceptionHandler(NoResourceFoundException.class)
    public String handlerException(NoResourceFoundException e, Model model, HttpServletResponse response) {
        model.addAttribute("err", "请求的资源 “" + e.getResourcePath() + "” 不存在");
        addModelAttributesForExceptionHandler(model);
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return "error";
    }

    // 全局异常拦截
    @ExceptionHandler
    public String handlerException(Exception e, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute("errTitle", "内部服务器错误");
        model.addAttribute("err", e.getMessage() + "<br><br>请稍后再试。如果此错误持续出现，请与站点管理员联系。");
        addModelAttributesForExceptionHandler(model);
        log.error("未处理的全局异常: {}", e.getMessage(), e);
        return "error";
    }

    private void addModelAttributesForExceptionHandler(Model model) {
        model.addAttribute("siteName", settingsService.getValue(Setting.SITE_NAME).orElseThrow());
        model.addAttribute("user", addUserToModel());
        model.addAttribute("userStats", addUserStatsToModel());
        model.addAttribute("timezone", viewerTimeZone());
    }

    @ModelAttribute("userStats")
    private UserStatsVO addUserStatsToModel() {
        if (!StpUtil.isLogin()) return null;
        try {
            var usr = userStatsService.getUserStats(StpUtil.getLoginIdAsLong());
            if (usr != null)
                return userStatsService.toVO(usr);
            return null;
        } catch (NotLoginException e) {
            return null;
        }
    }

    @ModelAttribute("timezone")
    public String viewerTimeZone() {
        if (!StpUtil.isLogin()) return settingsService.getValue(Setting.SITE_TIMEZONE).orElseThrow();
        try {
            var usr = usersService.getUserById(StpUtil.getLoginIdAsLong());
            if (usr == null) return settingsService.getValue(Setting.SITE_TIMEZONE).orElseThrow();
            return usr.getTimeZone();
        } catch (NotLoginException e) {
            return settingsService.getValue(Setting.SITE_TIMEZONE).orElseThrow();
        }
    }

    @ModelAttribute("user")
    public UserVO addUserToModel() {
        if (!StpUtil.isLogin()) return null;
        try {
            var usr = usersService.getUserById(StpUtil.getLoginIdAsLong());
            if (usr != null)
                return usersService.toVO(usr);
            return null;
        } catch (NotLoginException e) {
            return null;
        }
    }

    @ModelAttribute("siteName")
    public String addSiteName() {
        return settingsService.getValue(Setting.SITE_NAME).orElseThrow();
    }
}
