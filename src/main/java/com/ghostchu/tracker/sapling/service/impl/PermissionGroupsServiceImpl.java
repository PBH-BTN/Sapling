package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.PermissionGroups;
import com.ghostchu.tracker.sapling.mapper.PermissionGroupsMapper;
import com.ghostchu.tracker.sapling.service.IPermissionGroupsService;
import com.ghostchu.tracker.sapling.vo.PermissionGroupVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
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
public class PermissionGroupsServiceImpl extends MPJBaseServiceImpl<PermissionGroupsMapper, PermissionGroups> implements IPermissionGroupsService {
    @Override
    @Cacheable(value = "permissions_group", key = "'id:' + #id", unless = "#result == null")
    public PermissionGroups getPermissionGroupById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public PermissionGroupVO toVO(PermissionGroups groups) {
        PermissionGroupVO permissionGroupVO = new PermissionGroupVO();
        permissionGroupVO.setId(groups.getId() == null ? 0 : groups.getId());
        permissionGroupVO.setName(groups.getName());
        permissionGroupVO.setColor(groups.getColor());
        return permissionGroupVO;
    }

    @Override
    public IPage<PermissionGroups> pageGroups(int page, int size, String search) {
        IPage<PermissionGroups> pager = new Page<>(page, size);
        QueryWrapper<PermissionGroups> query = new QueryWrapper<>();
        if (StringUtils.isNotBlank(search)) {
            query = query.like("name", search);
        }
        return page(pager, query);
    }

    @Override
    @CacheEvict(value = "permissions_group", key = "'id:' + #id")
    public void saveGroup(PermissionGroups group) {
        saveOrUpdate(group);
    }

    @Override
    @CacheEvict(value = "permissions_group", key = "'id:' + #id")
    public void deleteGroup(Long id) {
        removeById(id);
    }
}
