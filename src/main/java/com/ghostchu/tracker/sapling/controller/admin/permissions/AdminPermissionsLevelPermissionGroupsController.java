package com.ghostchu.tracker.sapling.controller.admin.permissions;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.dto.LevelPermissionGroupFormDTO;
import com.ghostchu.tracker.sapling.entity.LevelPermissionGroups;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.ILevelPermissionGroupsService;
import com.ghostchu.tracker.sapling.vo.LevelPermissionGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/permissions/levelPermissionGroups")
@SaCheckPermission(Permission.ADMIN_PERMISSIONS_LEVEL_PERMISSION_GROUPS)
@SaCheckDisable
public class AdminPermissionsLevelPermissionGroupsController {

    @Autowired
    private ILevelPermissionGroupsService levelGroupService;

    @GetMapping
    public String groupList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            Model model) {

        IPage<LevelPermissionGroups> groupPage = levelGroupService.pageGroups(page, size, search);
        model.addAttribute("levelGroups", groupPage);
        return "admin/permissions/levelGroups";
    }

    @PostMapping
    public String saveGroup(@ModelAttribute LevelPermissionGroupFormDTO groupForm) {
        LevelPermissionGroups group = levelGroupService.getLevelPermissionGroupById(groupForm.getId());
        if (group == null) {
            group = new LevelPermissionGroups();
        }
        group.setName(groupForm.getName());
        group.setPriority(groupForm.getPriority());
        group.setColor(groupForm.getColor());
        levelGroupService.saveGroup(group);
        return "redirect:/admin/permissions/levelPermissionGroups";
    }

    @DeleteMapping("/{id}")
    public String deleteGroup(@PathVariable Long id) {
        levelGroupService.deleteGroup(id);
        return "redirect:/admin/permissions/levelPermissionGroups";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public LevelPermissionGroupVO getGroup(@PathVariable Long id) {
        return levelGroupService.toVO(levelGroupService.getLevelPermissionGroupById(id));
    }

}
