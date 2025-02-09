package com.ghostchu.tracker.sapling.vo;

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
public class UserTaskRecordsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private TorrentVO torrent;

    private UserVO owner;

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
