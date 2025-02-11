package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.ghostchu.tracker.sapling.dto.ConfirmFormDTO;
import com.ghostchu.tracker.sapling.dto.ProfileUpdateFormDTO;
import com.ghostchu.tracker.sapling.entity.Bitbucket;
import com.ghostchu.tracker.sapling.entity.UserStats;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.exception.UserNotExistsException;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.IBitbucketService;
import com.ghostchu.tracker.sapling.service.IUserBansService;
import com.ghostchu.tracker.sapling.service.IUserStatsService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.util.FileUtil;
import com.ghostchu.tracker.sapling.util.HtmlSanitizer;
import com.ghostchu.tracker.sapling.vo.UserVO;
import com.google.common.html.HtmlEscapers;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Controller
@RequestMapping("/users")
@SaCheckDisable
public class UsersController {
    @Autowired
    private IUsersService userService;
    @Autowired
    private IBitbucketService bitbucketService;
    @Autowired
    private IUserBansService userBansService;
    @Autowired
    private IUserStatsService userStatsService;

    // 查看当前用户资料
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        return "redirect:/users/profile/" + StpUtil.getLoginIdAsLong();
    }

    @GetMapping("/{id}/ban")
    @SaCheckPermission(value = {Permission.USER_BAN})
    public String banUser(Model model, @PathVariable long id) {
        Users users = userService.getUserById(id);
        if (users == null) {
            throw new UserNotExistsException(id, "指定的用户不存在");
        }
        model.addAttribute("userToBan", userService.toVO(users));
        return "users/ban";
    }

    @GetMapping("/{id}/unban")
    @SaCheckPermission(value = {Permission.USER_UNBAN})
    public String unbanUser(Model model, @PathVariable long id) {
        Users users = userService.getUserById(id);
        if (users == null) {
            throw new UserNotExistsException(id, "指定的用户不存在");
        }
        ConfirmFormDTO form = new ConfirmFormDTO();
        form.setActionUrl("/users/" + id + "/unban");
        form.setTitle("解除封禁");
        form.setHeaderClass("bg-warning text-white");
        form.setDescription("您确定要解除对用户 " + HtmlEscapers.htmlEscaper().escape(users.getName()) + " 的封禁吗？");
        form.setConfirmButtonText("解除");
        model.addAttribute("form", form);
        model.addAttribute("userToBan", userService.toVO(users));
        return "confirm";
    }

    // 处理封禁请求
    @PostMapping("/{id}/ban")
    @SaCheckPermission(value = {Permission.USER_BAN})
    public String processBanUser(
            @PathVariable long id,
            @RequestParam String reason) {
        Users users = userService.getUserById(id);
        if (users == null) {
            throw new UserNotExistsException(id, "指定的用户不存在");
        }
        userService.banUser(id, StpUtil.getLoginIdAsLong(), reason, OffsetDateTime.now().plusYears(100));
        return "redirect:/users/profile/" + id;
    }

    // 处理封禁请求
    @PostMapping("/{id}/unban")
    @SaCheckPermission(value = {Permission.USER_BAN})
    public String processUnbanUser(@PathVariable long id) {
        Users users = userService.getUserById(id);
        if (users == null) {
            throw new UserNotExistsException(id, "指定的用户不存在");
        }
        userService.unbanUser(id, StpUtil.getLoginIdAsLong());
        return "redirect:/users/profile/" + id;
    }

    // 查看其他用户资料
    @GetMapping("/profile/{id}")
    public String viewUserProfile(
            @PathVariable long id,
            Model model) {
        Users users = userService.getUserById(id);
        if (users == null) {
            throw new UserNotExistsException(id, "指定的用户不存在");
        }
        UserVO targetUser = userService.toVO(userService.getUserById(id));
        boolean isCurrentUser = targetUser.getId() == StpUtil.getLoginIdAsLong();
        model.addAttribute("user", targetUser);
        model.addAttribute("userStats", userStatsService.getUserStats(id));
        model.addAttribute("isCurrentUser", isCurrentUser);
        return "users/profile";
    }

    // 编辑用户资料
    @GetMapping("/profile/edit/{id}")
    @SaCheckPermission(value = {Permission.USER_EDIT, Permission.USER_EDIT_OTHER}, mode = SaMode.OR)
    public String editProfile(Model model, @PathVariable long id) {
        if (id == StpUtil.getLoginIdAsLong()) {
            StpUtil.checkPermission(Permission.USER_EDIT);
        } else {
            StpUtil.checkPermission(Permission.USER_EDIT_OTHER);
        }
        UserVO currentUser = userService.toVO(userService.getUserById(id));
        model.addAttribute("user", currentUser);
        model.addAttribute("isCurrentUser", true);
        model.addAttribute("form", convertToForm(currentUser));
        return "users/edit";
    }

    @PostMapping("/profile/edit/{id}")
    @Transactional
    @SaCheckPermission(value = {Permission.USER_EDIT, Permission.USER_EDIT_OTHER}, mode = SaMode.OR)
    public String updateProfile(
            @ModelAttribute @Valid ProfileUpdateFormDTO form,
            @PathVariable long id,
            BindingResult result,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile) throws IOException {
        if (result.hasErrors()) {
            return "users/edit";
        }
        if (id == StpUtil.getLoginIdAsLong()) {
            StpUtil.checkPermission(Permission.USER_EDIT);
        } else {
            StpUtil.checkPermission(Permission.USER_EDIT_OTHER);
        }
        Users users = userService.getUserByIdForUpdate(id);
        if (!form.getName().equals(users.getName()) && userService.userNameExists(form.getName())) {
            result.rejectValue("name", "user.name.exists", "给定的用户名已被使用");
            return "users/edit";
        }
        if (!(avatarFile.getContentType() == null || StringUtils.isBlank(avatarFile.getOriginalFilename()))) {
            Bitbucket bitbucket = bitbucketService.uploadToBitbucket(
                    avatarFile,
                    StpUtil.getLoginIdAsLong(),
                    FileUtil.mime(avatarFile.getOriginalFilename(), MediaType.IMAGE_PNG).toString(),
                    true, true);
            users.setAvatar("/bitbucket/file/" + bitbucket.getId());
        }
        users.setMyBandwidthUpload(form.getMyBandwidthUpload());
        users.setMyBandwidthDownload(form.getMyBandwidthDownload());
        users.setMyIsp(form.getMyIsp());
        users.setName(form.getName());
        users.setTitle(form.getTitle());
        users.setSignature(HtmlSanitizer.sanitize(form.getSignature()));
        if (StpUtil.hasPermission(Permission.USER_EDIT_DATA)) {
            UserStats userStats = userStatsService.selectUserStatsForUpdate(id);
            userStats.setUploaded(form.getUploaded());
            userStats.setUploadedReal(form.getUploadedReal());
            userStats.setDownloaded(form.getDownloaded());
            userStats.setDownloadedReal(form.getDownloadedReal());
            userStats.setSeedTime(form.getSeedTime());
            userStats.setLeechTime(form.getLeechTime());
            userStatsService.updateUserStats(userStats);
        }
        userService.updateUser(users);
        return "redirect:/users/profile/" + id;
    }

    private ProfileUpdateFormDTO convertToForm(UserVO user) {
        ProfileUpdateFormDTO form = new ProfileUpdateFormDTO();
        form.setId(user.getId());
        form.setName(user.getName());
        form.setTitle(user.getTitle());
        form.setSignature(user.getSignature());
        form.setAvatar(user.getAvatar());
        form.setMyBandwidthUpload(user.getMyBandwidthUpload());
        form.setMyBandwidthDownload(user.getMyBandwidthDownload());
        form.setMyIsp(user.getMyIsp());
        form.setUploaded(user.getUploaded());
        form.setUploadedReal(user.getUploadedReal());
        form.setDownloaded(user.getDownloaded());
        form.setDownloadedReal(user.getDownloadedReal());
        form.setSeedTime(user.getSeedTime());
        form.setLeechTime(user.getLeechTime());
        return form;
    }


}
