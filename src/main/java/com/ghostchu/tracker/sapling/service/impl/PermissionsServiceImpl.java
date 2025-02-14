package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.Permissions;
import com.ghostchu.tracker.sapling.mapper.PermissionsMapper;
import com.ghostchu.tracker.sapling.service.IPermissionsService;
import com.ghostchu.tracker.sapling.vo.PermissionsVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class PermissionsServiceImpl extends MPJBaseServiceImpl<PermissionsMapper, Permissions> implements IPermissionsService {
    //更改权限的时候需要刷新这两个缓存
    @Override
    @Cacheable(value = "permissions", key = "'target_group:' + #id", unless = "#result == null")
    public List<Permissions> getGroupPermissions(long id, boolean isLevelGroup) {
        return list(new QueryWrapper<Permissions>().eq("target_group", id).eq("target_is_level_group", isLevelGroup));
    }

    @Override
    public IPage<Permissions> pagePermissions(int page, int size, String search) {
        return null;
    }

    @Override
    @CacheEvict(value = "permissions", allEntries = true)
    public void savePermission(Permissions permission) {
        saveOrUpdate(permission);
    }

    @Override
    public void deletePermission(Long id) {
        removeById(id);
    }

    @Override
    public PermissionsVO toVO(Permissions byId) {
        PermissionsVO permissionsVO = new PermissionsVO();
        permissionsVO.setId(byId.getId());
        permissionsVO.setNode(byId.getNode());
        permissionsVO.setTargetGroup(byId.getTargetGroup());
        permissionsVO.setTargetIsLevelGroup(byId.isTargetIsLevelGroup());
        return permissionsVO;
    }
}
