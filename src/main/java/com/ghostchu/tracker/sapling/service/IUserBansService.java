package com.ghostchu.tracker.sapling.service;

import com.ghostchu.tracker.sapling.entity.UserBans;
import com.github.yulichang.base.MPJBaseService;

import java.time.OffsetDateTime;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface IUserBansService extends MPJBaseService<UserBans> {

    UserBans banUser(long id, long op, String reason, OffsetDateTime endedAt);

    UserBans getBanRecord(Long bannedId);
}
