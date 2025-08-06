package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.entity.UserBans;
import com.ghostchu.tracker.sapling.mapper.UserBansMapper;
import com.ghostchu.tracker.sapling.service.IUserBansService;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class UserBansServiceImpl extends MPJBaseServiceImpl<UserBansMapper, UserBans> implements IUserBansService {
    @Override
    public UserBans banUser(long id, long op, String reason, OffsetDateTime endedAt) {
        UserBans userBans = new UserBans();
        userBans.setOwner(id);
        userBans.setOperator(op);
        userBans.setCreatedAt(OffsetDateTime.now());
        userBans.setEndedAt(endedAt);
        userBans.setDescription(reason);
        saveOrUpdate(userBans);
        return userBans;
    }

    @Override
    @Cacheable(value = "userbans", key = "'id:' + #bannedId", unless = "#result == null")
    public UserBans getBanRecord(Long bannedId) {
        return getById(bannedId);
    }
}
