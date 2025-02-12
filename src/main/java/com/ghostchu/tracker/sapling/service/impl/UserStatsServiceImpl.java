package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    @Cacheable(value = "userStats", key = "'uid:' + #userId", unless = "#result == null")
    public UserStats getUserStats(long userId) {
        UserStats userStats = getOne(new QueryWrapper<UserStats>().eq("owner", userId));
        if (userStats == null) {
            userStats = new UserStats();
            userStats.setOwner(userId);
            save(userStats);
        }
        return userStats;
    }

    @Override
    @Cacheable(value = "userStats", key = "'uid:' + #userId", unless = "#result == null")
    public UserStats selectUserStatsForUpdate(long userId) {
        UserStats stats = baseMapper.selectUserStatsForUpdate(userId);
        if (stats == null) {
            stats = new UserStats();
            stats.setOwner(userId);
            save(stats);
            stats = baseMapper.selectUserStatsForUpdate(userId);
        }
        return stats;
    }

    @Override
    @CacheEvict(value = "userStats", key = "'uid:' + #userStats.owner")
    public void updateUserStats(UserStats userStats) {
        baseMapper.updateById(userStats);
    }

    @Override
    public UserStatsVO toVO(UserStats userStats) {
        UserStatsVO userStatsVO = new UserStatsVO();
        userStatsVO.setId(userStats.getId() == null ? 0 : userStats.getId());
        userStatsVO.setOwner(userStats.getOwner());
        userStatsVO.setUploaded(userStats.getUploaded());
        userStatsVO.setDownloaded(userStats.getDownloaded());
        userStatsVO.setUploadedReal(userStats.getUploadedReal());
        userStatsVO.setDownloadedReal(userStats.getDownloadedReal());
        userStatsVO.setSeedTime(userStats.getSeedTime());
        userStatsVO.setLeechTime(userStats.getLeechTime());
        userStatsVO.setSeedScore(userStats.getSeedScore());
        userStatsVO.setShareRatio(userStats.shareRatio());
        // x.xx
        String shareRatioString;
        if (userStats.shareRatio() == -1) {
            shareRatioString = "Inf.";
        } else {
            shareRatioString = String.format("%.2f", userStatsVO.getShareRatio());
        }
        userStatsVO.setShareRatioStr(shareRatioString);
        return userStatsVO;
    }
}
