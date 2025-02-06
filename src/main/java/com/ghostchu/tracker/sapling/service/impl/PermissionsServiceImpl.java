package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ghostchu.tracker.sapling.entity.Permissions;
import com.ghostchu.tracker.sapling.mapper.PermissionsMapper;
import com.ghostchu.tracker.sapling.service.IPermissionsService;
import com.github.yulichang.base.MPJBaseServiceImpl;
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

    @Override
    public List<Permissions> getGroupPermissions(long groupId) {
        return list(new QueryWrapper<Permissions>().eq("target_group", groupId));
    }
}
