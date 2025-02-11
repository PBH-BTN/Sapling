package com.ghostchu.tracker.sapling.mapper;

import com.ghostchu.tracker.sapling.entity.UserTasks;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface UserTasksMapper extends SaplingMapper<UserTasks> {

    UserTasks selectUserTaskRecordsForUpdate(@Param("owner") long owner, @Param("torrent") long torrent);
}

