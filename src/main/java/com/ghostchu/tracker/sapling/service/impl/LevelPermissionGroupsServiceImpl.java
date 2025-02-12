package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.entity.LevelPermissionGroups;
import com.ghostchu.tracker.sapling.mapper.LevelPermissionGroupsMapper;
import com.ghostchu.tracker.sapling.service.ILevelPermissionGroupsService;
import com.ghostchu.tracker.sapling.vo.LevelPermissionGroupVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class LevelPermissionGroupsServiceImpl extends MPJBaseServiceImpl<LevelPermissionGroupsMapper, LevelPermissionGroups> implements ILevelPermissionGroupsService {
    @Override
    @Cacheable(value = "level_permissions_group", key = "'id:' + #id", unless = "#result == null")
    public LevelPermissionGroups getLevelPermissionGroupById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public LevelPermissionGroupVO toVO(LevelPermissionGroups groups) {
        LevelPermissionGroupVO permissionGroupVO = new LevelPermissionGroupVO();
        permissionGroupVO.setId(groups.getId() == null ? 0 : groups.getId());
        permissionGroupVO.setName(groups.getName());
        permissionGroupVO.setColor(groups.getColor());
        return permissionGroupVO;
    }
}
