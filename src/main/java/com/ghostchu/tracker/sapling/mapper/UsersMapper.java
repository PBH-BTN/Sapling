package com.ghostchu.tracker.sapling.mapper;

import com.ghostchu.tracker.sapling.entity.Users;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface UsersMapper extends SaplingMapper<Users> {
    Users selectUserForUpdate(@Param("id") long id);
}

