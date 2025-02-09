package com.ghostchu.tracker.sapling.mapper;

import com.ghostchu.tracker.sapling.entity.UserTaskRecords;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface UserTaskRecordsMapper extends SaplingMapper<UserTaskRecords> {

    UserTaskRecords selectUserTaskRecordsForUpdate(@Param("owner") long owner, @Param("torrent") long torrent);
}

