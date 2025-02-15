package com.ghostchu.tracker.sapling.mapper;

import com.ghostchu.tracker.sapling.entity.UserBalances;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface UserBalancesMapper extends SaplingMapper<UserBalances> {
    UserBalances selectUserBalanceForUpdate(@Param("id") Long id);

    UserBalances selectUserBalancesForUpdate(@Param("owner") Long owner, @Param("currency") Long currency);
}

