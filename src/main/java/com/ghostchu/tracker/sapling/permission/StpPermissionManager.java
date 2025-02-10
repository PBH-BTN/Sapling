package com.ghostchu.tracker.sapling.permission;

import cn.dev33.satoken.model.wrapperInfo.SaDisableWrapperInfo;
import cn.dev33.satoken.stp.StpInterface;
import com.ghostchu.tracker.sapling.entity.*;
import com.ghostchu.tracker.sapling.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
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
    @Autowired
    private IUserBansService userBansService;

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

    @Override
    public SaDisableWrapperInfo isDisabled(Object loginId, String service) {
        Users users = usersService.getById(Long.parseLong((String) loginId));
        if (users.getBannedId() == null) return new SaDisableWrapperInfo(false, 0, 0);
        UserBans userBans = userBansService.getBanRecord(users.getBannedId());
        if (userBans == null) {
            usersService.unbanUser(users.getId(), 1);
            return new SaDisableWrapperInfo(false, 0, 0);
        }
        if (userBans.getEndedAt().isBefore(OffsetDateTime.now())) {
            usersService.unbanUser(users.getId(), 1);
            return new SaDisableWrapperInfo(false, 0, 0);
        }
        return new SaDisableWrapperInfo(true, userBans.getEndedAt().toEpochSecond(), 1);
    }
}
