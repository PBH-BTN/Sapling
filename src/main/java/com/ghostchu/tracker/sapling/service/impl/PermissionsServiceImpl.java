package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ghostchu.tracker.sapling.entity.Permissions;
import com.ghostchu.tracker.sapling.mapper.PermissionsMapper;
import com.ghostchu.tracker.sapling.service.IPermissionsService;
import com.github.yulichang.base.MPJBaseServiceImpl;
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
    @Cacheable(value = "permissions", key = "'id:' + #id", unless = "#result == null")
    public List<Permissions> getGroupPermissions(long id) {
        return list(new QueryWrapper<Permissions>().eq("target_group", id));
    }
}
