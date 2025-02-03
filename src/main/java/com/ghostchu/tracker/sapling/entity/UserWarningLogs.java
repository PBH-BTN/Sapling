package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
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
@TableName("user_warning_logs")
public class UserWarningLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户警告日志ID
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
     * 警告操作时间
     */
    private LocalDateTime createdAt;

    /**
     * 警告结束时间
     */
    private LocalDateTime endedAt;

    /**
     * 警告原因描述
     */
    private String description;

    /**
     * 警告活动状态
     */
    private Boolean active;
}
