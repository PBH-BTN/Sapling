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
@TableName("user_bans")
public class UserBans implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户封禁日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户ID
     */
    private long owner;

    /**
     * 操作员ID
     */
    private long operator;

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
}
