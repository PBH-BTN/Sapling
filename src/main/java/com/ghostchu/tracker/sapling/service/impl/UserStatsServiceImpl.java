package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.entity.UserStats;
import com.ghostchu.tracker.sapling.mapper.UserStatsMapper;
import com.ghostchu.tracker.sapling.service.IUserStatsService;
import com.ghostchu.tracker.sapling.vo.UserStatsVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
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
public class UserStatsServiceImpl extends MPJBaseServiceImpl<UserStatsMapper, UserStats> implements IUserStatsService {
    @Override
    @Cacheable(value = "userStats", key = "'id:' + #id", unless = "#result == null")
    public UserStats getUserStats(long id) {
        UserStats userStats = baseMapper.selectById(id);
        if (userStats == null) {
            userStats = new UserStats();
            save(userStats);
        }
        return userStats;
    }

    @Override
    @Cacheable(value = "userStats", key = "'id:' + #id", unless = "#result == null")
    public UserStats selectUserStatsForUpdate(long id) {
        UserStats stats = baseMapper.selectUserStatsForUpdate(id);
        if (stats == null) {
            stats = new UserStats();
            save(stats);
            stats = baseMapper.selectUserStatsForUpdate(id);
        }
        return stats;
    }

    @Override
    @CacheEvict(value = "userStats", key = "'id:' + #userStats.getId()")
    public void updateUserStats(UserStats userStats) {
        baseMapper.updateById(userStats);
    }

    @Override
    public UserStatsVO toVO(UserStats userStats) {
        UserStatsVO userStatsVO = new UserStatsVO();
        userStatsVO.setId(userStats.getId());
        userStatsVO.setOwner(userStats.getOwner());
        userStatsVO.setUploaded(userStats.getUploaded());
        userStatsVO.setDownloaded(userStats.getDownloaded());
        userStatsVO.setUploadedReal(userStats.getUploadedReal());
        userStatsVO.setDownloadedReal(userStats.getDownloadedReal());
        userStatsVO.setSeedTime(userStats.getSeedTime());
        userStatsVO.setLeechTime(userStats.getLeechTime());
        userStatsVO.setSeedScore(userStats.getSeedScore());
        return userStatsVO;
    }
}
