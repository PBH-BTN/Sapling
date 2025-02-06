package com.ghostchu.tracker.sapling.service;

import com.ghostchu.tracker.sapling.entity.LevelPermissionGroups;
import com.ghostchu.tracker.sapling.vo.LevelPermissionGroupVO;
import com.github.yulichang.base.MPJBaseService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface ILevelPermissionGroupsService extends MPJBaseService<LevelPermissionGroups> {

    LevelPermissionGroups getLevelPermissionGroupById(Long id);

    LevelPermissionGroupVO toVO(LevelPermissionGroups groups);
}
