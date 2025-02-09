package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Getter
@Setter
@ToString
@TableName("user_task_records")
public class UserTaskRecords implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;

    private long torrent;

    private long owner;

    private long toGo;

    private OffsetDateTime lastAnnounce;

    private String lastEvent;

    private long uploaded;

    private long downloaded;

    private long seedTime;

    private long leechTime;

    private OffsetDateTime finishedAt;

    private String userAgent;

}
