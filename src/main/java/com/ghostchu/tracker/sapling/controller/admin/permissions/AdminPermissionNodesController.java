package com.ghostchu.tracker.sapling.controller.admin.permissions;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.dto.PermissionsFormDTO;
import com.ghostchu.tracker.sapling.entity.Permissions;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.IPermissionsService;
import com.ghostchu.tracker.sapling.vo.PermissionsVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/permissions/permissionNodes")
@SaCheckPermission(Permission.ADMIN_PERMISSIONS_PERMISSION_NODES)
@SaCheckDisable
public class AdminPermissionNodesController {

    private final IPermissionsService permissionService;

    @GetMapping
    public String permissionList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            Model model) {
        IPage<Permissions> permPage = permissionService.pagePermissions(page, size, search);
        model.addAttribute("permissions", permPage);
        return "admin/permissions/permissionNodes";
    }

    @PostMapping
    public String savePermission(@ModelAttribute PermissionsFormDTO permissionVO) {
        Permissions permission;
        if (permissionVO.getId() == null) {
            permission = new Permissions();
        } else {
            permission = permissionService.getById(permissionVO.getId());
        }
        permission.setNode(permissionVO.getNode());
        permission.setTargetGroup(permissionVO.getTargetGroup());
        permission.setTargetIsLevelGroup(permissionVO.isTargetIsLevelGroup());
        permissionService.savePermission(permission);
        return "redirect:/admin/permissions/permissionNodes";
    }

    @DeleteMapping("/{id}")
    public String deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return "redirect:/admin/permissions/permissionNodes";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public PermissionsVO getPermission(@PathVariable Long id) {
        return permissionService.toVO(permissionService.getById(id));
    }
}
