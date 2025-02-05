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
@TableName("user_warning_logs")
public class UserWarningLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户警告日志ID
     */
    @TableId(type = IdType.AUTO)
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
     * 警告操作时间
     */
    private OffsetDateTime createdAt;

    /**
     * 警告结束时间
     */
    private OffsetDateTime endedAt;

    /**
     * 警告原因描述
     */
    private String description;

    /**
     * 警告活动状态
     */
    private boolean active;
}
