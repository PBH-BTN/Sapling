package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.ghostchu.tracker.sapling.dto.ProfileUpdateFormDTO;
import com.ghostchu.tracker.sapling.entity.Bitbucket;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.exception.UserNotExistsException;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.IBitbucketService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.util.FileUtil;
import com.ghostchu.tracker.sapling.util.HtmlSanitizer;
import com.ghostchu.tracker.sapling.vo.UserVO;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
public class UsersController {
    @Autowired
    private IUsersService userService;
    @Autowired
    private IBitbucketService bitbucketService;

    // 查看当前用户资料
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        return "redirect:/users/profile/" + StpUtil.getLoginIdAsLong();
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
        Users users = userService.getUserById(id);
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
            users.setUploaded(form.getUploaded());
            users.setUploadedReal(form.getUploadedReal());
            users.setDownloaded(form.getDownloaded());
            users.setDownloadedReal(form.getDownloadedReal());
            users.setSeedTime(form.getSeedTime());
            users.setLeechTime(form.getLeechTime());
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
