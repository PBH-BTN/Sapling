package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.entity.UserTasks;
import com.ghostchu.tracker.sapling.mapper.UserTasksMapper;
import com.ghostchu.tracker.sapling.service.IUserTasksService;
import com.ghostchu.tracker.sapling.tracker.PeerEvent;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.retry.annotation.Retryable;
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
public class UserTasksServiceImpl extends MPJBaseServiceImpl<UserTasksMapper, UserTasks> implements IUserTasksService {
    @Override
    @Retryable
    public UserTasks updateUserTaskRecord(long owner, long torrent, long toGo, OffsetDateTime announceAt, PeerEvent event, long incrementUploaded,
                                          long incrementDownloaded, long realIncrementUploaded, long realIncrementDownloaded, long incrementSeedTime, long incrementLeechTime, String userAgent) {
        UserTasks userTasks = this.baseMapper.selectUserTaskRecordsForUpdate(owner, torrent);
        if (userTasks == null) {
            userTasks = new UserTasks();
            userTasks.setOwner(owner);
            userTasks.setTorrent(torrent);
        }
        userTasks.setToGo(toGo);
        userTasks.setLastAnnounce(announceAt);
        userTasks.setLastEvent(event.name());
        userTasks.setUploaded(userTasks.getUploaded() + realIncrementUploaded);
        userTasks.setDownloaded(userTasks.getDownloaded() + realIncrementDownloaded);
        userTasks.setSeedTime(userTasks.getSeedTime() + incrementSeedTime);
        userTasks.setLeechTime(userTasks.getLeechTime() + incrementLeechTime);
        if (event == PeerEvent.COMPLETED || toGo == 0) {
            userTasks.setFinishedAt(announceAt);
        }
        userTasks.setUserAgent(userAgent);
        saveOrUpdate(userTasks);
        return userTasks;
    }
}
