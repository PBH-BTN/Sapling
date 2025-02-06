package com.ghostchu.tracker.sapling.service;

import com.ghostchu.tracker.sapling.entity.Permissions;
import com.github.yulichang.base.MPJBaseService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface IPermissionsService extends MPJBaseService<Permissions> {

    List<Permissions> getGroupPermissions(long groupId);

}
