package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@TableName("user_ban_logs")
public class UserBanLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户封禁日志ID
     */
    private Long id;

    /**
     * 关联用户ID
     */
    private Long owner;

    /**
     * 操作员ID
     */
    private Long operator;

    /**
     * 封禁操作时间
     */
    private OffsetDateTime createdAt;

    /**
     * 封禁结束时间
     */
    private OffsetDateTime endedAt;

    /**
     * 封禁原因描述
     */
    private String description;

    /**
     * 封禁活动状态
     */
    private Boolean active;
}
