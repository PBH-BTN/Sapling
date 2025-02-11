package com.ghostchu.tracker.sapling.service;

import com.ghostchu.tracker.sapling.entity.UserStats;
import com.ghostchu.tracker.sapling.vo.UserStatsVO;
import com.github.yulichang.base.MPJBaseService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface IUserStatsService extends MPJBaseService<UserStats> {

    UserStats getUserStats(long id);

    UserStats selectUserStatsForUpdate(long id);

    void updateUserStats(UserStats userStats);

    UserStatsVO toVO(UserStats userStats);
}
