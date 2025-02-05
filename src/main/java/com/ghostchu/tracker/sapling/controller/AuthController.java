package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ghostchu.tracker.sapling.dto.LoginForm;
import com.ghostchu.tracker.sapling.dto.RegForm;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.util.SecretUtil;
import com.ghostchu.tracker.sapling.util.ServletUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.OffsetDateTime;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final IUsersService usersService;
    @Autowired
    private HttpServletRequest request;

    public AuthController(IUsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("username", "");
        model.addAttribute("password", "");
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid LoginForm loginForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        var matchedUser = loginViaCredentials(
                loginForm.getUsername(),
                loginForm.getPassword()
        );
        if (matchedUser == null) {
            redirectAttributes.addAttribute("error", true); // 添加 URL 参数
            return "redirect:/login"; // 重定向到登录页（会保留参数）
        }
        matchedUser.setLastLoginAt(OffsetDateTime.now());
        matchedUser.setLastLoginIp(ServletUtil.inet(request));
        usersService.updateById(matchedUser);
        StpUtil.login(matchedUser.getId(), loginForm.isRememberMe());
        return "redirect:/"; // 跳转到首页
    }

    private Users loginViaCredentials(String username, String password) {
        return usersService.getUserByUsernameAndPasswordHash(username, SecretUtil.hashPassword(password));
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerForm", new RegForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid RegForm regForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (usersService.userEmailExists(regForm.getEmail())) {
            redirectAttributes.addAttribute("error", "电子邮件地址已被使用。已经注册？尝试登录。");
            return "register";
        }
        if (usersService.userNameExists(regForm.getUsername())) {
            redirectAttributes.addAttribute("error", "用户名已被占用，换个名字吧！");
            return "register";
        }
        var passhash = SecretUtil.hashPassword(regForm.getPassword());
        boolean success = usersService.registerUser(
                regForm.getUsername(),
                passhash,
                regForm.getEmail(),
                ServletUtil.inet(request)
        );
        if (!success) {
            redirectAttributes.addAttribute("error", "注册失败，遇到了非预期错误，请稍后再试。");
            return "register";
        }
        StpUtil.login(usersService.getUserByUsernameAndPasswordHash(regForm.getUsername(), passhash).getId());
        return "redirect:/";
    }

}
