package com.ghostchu.tracker.sapling.controller.admin.permissions;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.dto.PermissionGroupsFormDTO;
import com.ghostchu.tracker.sapling.entity.PermissionGroups;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.IPermissionGroupsService;
import com.ghostchu.tracker.sapling.vo.PermissionGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/permissions/primaryPermissionGroups")
@SaCheckPermission(Permission.ADMIN_PERMISSIONS_PRIMARY_PERMISSION_GROUPS)
@SaCheckDisable
public class AdminPermissionsPrimaryPermissionGroupsController {

    @Autowired
    private IPermissionGroupsService permissionGroupService;

    @GetMapping
    public String groupList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            Model model) {

        IPage<PermissionGroups> groupPage = permissionGroupService.pageGroups(page, size, search);
        IPage<PermissionGroupVO> groupVOPage = groupPage.convert(permissionGroupService::toVO);
        model.addAttribute("primaryGroups", groupVOPage);
        return "admin/permissions/primaryGroups";
    }

    @PostMapping
    public String saveGroup(@ModelAttribute PermissionGroupsFormDTO groupVO) {
        PermissionGroups group = permissionGroupService.getPermissionGroupById(groupVO.getId());
        if (group == null) {
            group = new PermissionGroups();
        }
        group.setName(groupVO.getName());
        group.setPriority(groupVO.getPriority());
        group.setColor(groupVO.getColor());
        permissionGroupService.saveGroup(group);
        return "redirect:/admin/permissions/primaryPermissionGroups";
    }

    @DeleteMapping("/{id}")
    public String deleteGroup(@PathVariable Long id) {
        permissionGroupService.deleteGroup(id);
        return "redirect:/admin/permissions/primaryPermissionGroups";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public PermissionGroupVO getGroup(@PathVariable Long id) {
        return permissionGroupService.toVO(permissionGroupService.getPermissionGroupById(id));
    }

}
