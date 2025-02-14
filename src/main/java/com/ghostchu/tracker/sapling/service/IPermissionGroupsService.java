package com.ghostchu.tracker.sapling.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.PermissionGroups;
import com.ghostchu.tracker.sapling.vo.PermissionGroupVO;
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
public interface IPermissionGroupsService extends MPJBaseService<PermissionGroups> {

    PermissionGroups getPermissionGroupById(Long id);

    PermissionGroupVO toVO(PermissionGroups groups);

    List<PermissionGroups> listGroups();

    IPage<PermissionGroups> pageGroups(int page, int size, String search);

    void saveGroup(PermissionGroups group);

    void deleteGroup(Long id);

}
