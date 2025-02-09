package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.entity.UserTaskRecords;
import com.ghostchu.tracker.sapling.mapper.UserTaskRecordsMapper;
import com.ghostchu.tracker.sapling.service.IUserTaskRecordsService;
import com.ghostchu.tracker.sapling.tracker.PeerEvent;
import com.github.yulichang.base.MPJBaseServiceImpl;
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
public class UserTaskRecordsServiceImpl extends MPJBaseServiceImpl<UserTaskRecordsMapper, UserTaskRecords> implements IUserTaskRecordsService {
    @Override
    public UserTaskRecords updateUserTaskRecord(long owner, long torrent, long toGo, OffsetDateTime announceAt, PeerEvent event, long incrementUploaded,
                                                long incrementDownloaded, long incrementSeedTime, long incrementLeechTime, String userAgent) {
        UserTaskRecords userTaskRecords = this.baseMapper.selectUserTaskRecordsForUpdate(owner, torrent);
        if (userTaskRecords == null) {
            userTaskRecords = new UserTaskRecords();
            userTaskRecords.setOwner(owner);
            userTaskRecords.setTorrent(torrent);
        }
        userTaskRecords.setToGo(toGo);
        userTaskRecords.setLastAnnounce(announceAt);
        userTaskRecords.setLastEvent(event.name());
        userTaskRecords.setUploaded(userTaskRecords.getUploaded() + incrementUploaded);
        userTaskRecords.setDownloaded(userTaskRecords.getDownloaded() + incrementDownloaded);
        userTaskRecords.setSeedTime(userTaskRecords.getSeedTime() + incrementSeedTime);
        userTaskRecords.setLeechTime(userTaskRecords.getLeechTime() + incrementLeechTime);
        if (event == PeerEvent.COMPLETED || toGo == 0) {
            userTaskRecords.setFinishedAt(announceAt);
        }
        userTaskRecords.setUserAgent(userAgent);
        saveOrUpdate(userTaskRecords);
        return userTaskRecords;
    }
}
