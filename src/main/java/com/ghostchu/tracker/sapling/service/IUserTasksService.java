package com.ghostchu.tracker.sapling.service;

import com.ghostchu.tracker.sapling.entity.UserTasks;
import com.ghostchu.tracker.sapling.tracker.PeerEvent;
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
public interface IUserTasksService extends MPJBaseService<UserTasks> {

    UserTasks updateUserTaskRecord(long owner, long torrent, long toGo, OffsetDateTime announceAt, PeerEvent event,
                                   long incrementUploaded,
                                   long incrementDownloaded,
                                   long realIncrementUploaded,
                                   long realIncrementDownloaded,
                                   long incrementSeedTime, long incrementLeechTime, String userAgent);
}
