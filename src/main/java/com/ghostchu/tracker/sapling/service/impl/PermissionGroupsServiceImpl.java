package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.entity.PermissionGroups;
import com.ghostchu.tracker.sapling.mapper.PermissionGroupsMapper;
import com.ghostchu.tracker.sapling.service.IPermissionGroupsService;
import com.ghostchu.tracker.sapling.vo.PermissionGroupVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
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
public class PermissionGroupsServiceImpl extends MPJBaseServiceImpl<PermissionGroupsMapper, PermissionGroups> implements IPermissionGroupsService {
    @Override
    public PermissionGroups getPermissionGroupById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public PermissionGroupVO toVO(PermissionGroups groups) {
        PermissionGroupVO permissionGroupVO = new PermissionGroupVO();
        permissionGroupVO.setId(groups.getId());
        permissionGroupVO.setName(groups.getName());
        permissionGroupVO.setColor(groups.getColor());
        return permissionGroupVO;
    }
}
