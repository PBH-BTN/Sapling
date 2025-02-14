package com.ghostchu.tracker.sapling.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.Permissions;
import com.ghostchu.tracker.sapling.vo.PermissionsVO;
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

    List<Permissions> getGroupPermissions(long id, boolean isLevelGroup);

    IPage<Permissions> pagePermissions(int page, int size, String search);

    void savePermission(Permissions permission);

    void deletePermission(Long id);

    PermissionsVO toVO(Permissions byId);
}
