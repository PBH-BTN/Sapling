package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ghostchu.tracker.sapling.dto.LoginFormDTO;
import com.ghostchu.tracker.sapling.dto.RegFormDTO;
import com.ghostchu.tracker.sapling.entity.Invites;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.service.IInvitesService;
import com.ghostchu.tracker.sapling.service.ISettingsService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.service.impl.InvitesServiceImpl;
import com.ghostchu.tracker.sapling.util.SecretUtil;
import com.ghostchu.tracker.sapling.util.ServletUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.OffsetDateTime;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private IUsersService usersService;
    @Autowired
    private ISettingsService settingsService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private IInvitesService invitesService;
    @Autowired
    private InvitesServiceImpl invitesServiceImpl;


    @GetMapping("/login")
    public String login(Model model) {
        if (StpUtil.isLogin()) {
            return "redirect:/";
        }
        model.addAttribute("username", "");
        model.addAttribute("password", "");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(Model model) {
        StpUtil.logout();
        return "redirect:/auth/login";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid LoginFormDTO loginFormDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        var matchedUser = loginViaCredentials(
                loginFormDTO.getUsername(),
                loginFormDTO.getPassword()
        );
        if (matchedUser == null) {
            redirectAttributes.addAttribute("error", true); // 添加 URL 参数
            return "redirect:/auth/login"; // 重定向到登录页（会保留参数）
        }
        matchedUser.setLastLoginAt(OffsetDateTime.now());
        matchedUser.setLastLoginIp(ServletUtil.inet(request));
        usersService.updateById(matchedUser);
        StpUtil.login(matchedUser.getId(), loginFormDTO.isRememberMe());
        return "redirect:/"; // 跳转到首页
    }

    private Users loginViaCredentials(String username, String password) {
        return usersService.getUserByUsernameAndPasswordHash(username, SecretUtil.hashPassword(password));
    }

    @GetMapping("/register")
    public String register(Model model, @RequestParam(required = false) String inviteCode) {
        Invites invite = null;
        if (inviteCode != null) {
            invite = invitesService.getInviteByCode(inviteCode);
        }
        if (!isPublicRegisterAllowed() && (invite == null || !invite.isInviteValid())) {
            return disallowPublicRegister(model);
        }
        var regFormDTO = new RegFormDTO();
        if (invite != null) {
            regFormDTO.setInviteCode(inviteCode);
            regFormDTO.setEmail(invite.getInviteEmail());
            regFormDTO.setUsername(invite.getInviteUsername());
        }
        model.addAttribute("registerForm", regFormDTO);
        return "register";
    }

    @PostMapping("/register")
    @Transactional
    public String register(@Valid RegFormDTO regFormDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        Invites invite = null;
        if (regFormDTO.getInviteCode() != null) {
            invite = invitesService.getInviteByCode(regFormDTO.getInviteCode());
        }
        if (!isPublicRegisterAllowed() && (invite == null || !invite.isInviteValid())) {
            return disallowPublicRegister(model);
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (usersService.userEmailExists(regFormDTO.getEmail())) {
            redirectAttributes.addAttribute("error", "电子邮件地址已被使用。已经注册？尝试登录。");
            return "register";
        }
        if (usersService.userNameExists(regFormDTO.getUsername())) {
            redirectAttributes.addAttribute("error", "用户名已被占用，换个名字吧！");
            return "register";
        }
        var passhash = SecretUtil.hashPassword(regFormDTO.getPassword());
        Users registered = usersService.registerUser(
                invite == null ? regFormDTO.getUsername() : invite.getInviteUsername(),
                passhash,
                invite == null ? regFormDTO.getEmail() : invite.getInviteEmail(),
                ServletUtil.inet(request)
        );
        if (!invitesService.markInviteAsUsed(invite, registered)) {
            // 抛出运行时异常，触发事务回滚
            throw new IllegalStateException("邀请码使用失败，可能已经过期或者被使用（并发注册？）");
        }
        StpUtil.login(registered.getId());
        return "redirect:/";
    }

    private String disallowPublicRegister(Model model) {
        model.addAttribute("errTitle", "自由注册已关闭");
        model.addAttribute("err", """
                很抱歉，自由注册当前关闭，只允许邀请注册。<br>
                如果你想加入，请找到能够邀请你进入本站的朋友:)<br>
                <br>
                我们只想知道有多少作弊者和吸血鬼在被封禁后才开始想到珍惜帐户。<br>
                请具备邀请资格的用户注意，如果你在知情的情况下将邀请发给作弊者和行为不端者，你和被邀请者都会被封禁。<br>
                如果你想重新启用帐户必须经过我们同意。
                """);
        return "error";
    }

    private boolean isPublicRegisterAllowed() {
        return Boolean.parseBoolean(settingsService.getValue("user.register.public").orElseThrow());
    }

    private boolean inviteCodeIsValid(String inviteCode) {

        return false;
    }

}
