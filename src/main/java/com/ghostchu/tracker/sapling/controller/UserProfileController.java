package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.ghostchu.tracker.sapling.dto.ProfileUpdateFormDTO;
import com.ghostchu.tracker.sapling.entity.Bitbucket;
import com.ghostchu.tracker.sapling.entity.PermissionGroups;
import com.ghostchu.tracker.sapling.entity.UserStats;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.exception.UserNotExistsException;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.*;
import com.ghostchu.tracker.sapling.util.FileUtil;
import com.ghostchu.tracker.sapling.util.HtmlSanitizer;
import com.ghostchu.tracker.sapling.vo.UserVO;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.MimeTypeUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/users/profile")
@SaCheckDisable
public class UserProfileController {
    @Autowired
    private IUsersService userService;
    @Autowired
    private IBitbucketService bitbucketService;
    @Autowired
    private IUserStatsService userStatsService;
    @Autowired
    private IPermissionGroupsService permissionGroupsService;
    @Autowired
    private IUserBalancesService userBalancesService;

    // 查看当前用户资料
    @GetMapping
    public String viewProfile() {
        return "redirect:/users/profile/" + StpUtil.getLoginIdAsLong();
    }

    // 查看其他用户资料
    @GetMapping("/{id}")
    public String viewUserProfile(
            @PathVariable long id,
            Model model) {
        Users users = userService.getUserById(id);
        if (users == null) {
            throw new UserNotExistsException(id, "指定的用户不存在");
        }
        UserVO targetUser = userService.toVO(userService.getUserById(id));
        boolean isCurrentUser = targetUser.getId() == StpUtil.getLoginIdAsLong();
        model.addAttribute("viewUser", targetUser);
        model.addAttribute("viewUserStats", userStatsService.toVO(userStatsService.getUserStats(id)));
        model.addAttribute("viewUserBalances", userBalancesService.toVO(userBalancesService.getUserBalances(id)));
        model.addAttribute("isCurrentUser", isCurrentUser);
        return "users/profile";
    }

    // 编辑用户资料
    @GetMapping("/edit/{id}")
    @SaCheckPermission(value = {Permission.USER_EDIT, Permission.USER_EDIT_OTHER}, mode = SaMode.OR)
    public String editProfile(Model model, @PathVariable long id) {
        if (id == StpUtil.getLoginIdAsLong()) {
            StpUtil.checkPermission(Permission.USER_EDIT);
        } else {
            StpUtil.checkPermission(Permission.USER_EDIT_OTHER);
        }
        UserVO currentUser = userService.toVO(userService.getUserById(id));
        model.addAttribute("viewUser", currentUser);
        model.addAttribute("isCurrentUser", true);
        model.addAttribute("viewUserForm", convertToForm(currentUser));
        model.addAttribute("viewSelectedPrimaryGroup", currentUser.getPrimaryPermissionGroup());
        model.addAttribute("viewPrimaryGroups", permissionGroupsService.listGroups().stream().map(permissionGroupsService::toVO).toList());
        return "users/edit";
    }

    @PostMapping("/edit/{id}")
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
            if (MimeTypeUtils.parseMimeType(avatarFile.getContentType()).getType().equals("image")) {
                Bitbucket bitbucket = bitbucketService.uploadToBitbucket(
                        avatarFile,
                        StpUtil.getLoginIdAsLong(),
                        FileUtil.mime(avatarFile.getOriginalFilename(), MediaType.IMAGE_PNG).toString(),
                        true, true);
                users.setAvatar("/bitbucket/file/" + bitbucket.getId());
            }
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
        if (StpUtil.hasPermission(Permission.USER_EDIT_PRIMARY_PERMISSION_GROUP)) {
            PermissionGroups permissionGroups = permissionGroupsService.getPermissionGroupById(form.getPrimaryGroupId());
            if (permissionGroups != null) {
                users.setPrimaryPermissionGroup(permissionGroups.getId());
            }
        }
        userService.updateUser(users);
        return "redirect:/users/profile/" + id;
    }

    private ProfileUpdateFormDTO convertToForm(UserVO user) {
        UserStats userStats = userStatsService.getUserStats(user.getId());
        ProfileUpdateFormDTO form = new ProfileUpdateFormDTO();
        form.setId(user.getId());
        form.setName(user.getName());
        form.setTitle(user.getTitle());
        form.setSignature(user.getSignature());
        form.setAvatar(user.getAvatar());
        form.setMyBandwidthUpload(user.getMyBandwidthUpload());
        form.setMyBandwidthDownload(user.getMyBandwidthDownload());
        form.setMyIsp(user.getMyIsp());
        form.setUploaded(userStats.getUploaded());
        form.setUploadedReal(userStats.getUploadedReal());
        form.setDownloaded(userStats.getDownloaded());
        form.setDownloadedReal(userStats.getDownloadedReal());
        form.setSeedTime(userStats.getSeedTime());
        form.setLeechTime(userStats.getLeechTime());
        return form;
    }

}
