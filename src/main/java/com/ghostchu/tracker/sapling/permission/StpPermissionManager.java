package com.ghostchu.tracker.sapling.permission;

import cn.dev33.satoken.stp.StpInterface;
import com.ghostchu.tracker.sapling.entity.LevelPermissionGroups;
import com.ghostchu.tracker.sapling.entity.PermissionGroups;
import com.ghostchu.tracker.sapling.entity.Permissions;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.service.ILevelPermissionGroupsService;
import com.ghostchu.tracker.sapling.service.IPermissionGroupsService;
import com.ghostchu.tracker.sapling.service.IPermissionsService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StpPermissionManager implements StpInterface {
    @Autowired
    private IPermissionsService permissionsService;
    @Autowired
    private IPermissionGroupsService permissionGroupsService;
    @Autowired
    private ILevelPermissionGroupsService levelPermissionGroupsService;
    @Autowired
    private IUsersService usersService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Users users = usersService.getById(Long.parseLong((String) loginId));
        List<Permissions> permissions = new ArrayList<>();
        permissions.addAll(permissionsService.getGroupPermissions(users.getPrimaryPermissionGroup(), false));
        permissions.addAll(permissionsService.getGroupPermissions(users.getLevelPermissionGroup(), true));
        return permissions.stream().map(Permissions::getNode).distinct().toList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Users users = usersService.getById(Long.parseLong((String) loginId));
        PermissionGroups primary = permissionGroupsService.getById(users.getPrimaryPermissionGroup());
        LevelPermissionGroups level = levelPermissionGroupsService.getById(users.getLevelPermissionGroup());
        return List.of(primary.getName(), level.getName());
    }
}
