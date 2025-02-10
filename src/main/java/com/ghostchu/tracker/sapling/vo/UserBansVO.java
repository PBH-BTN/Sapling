package com.ghostchu.tracker.sapling.vo;

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
public class UserBansVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户封禁日志ID
     */
    private Long id;

    /**
     * 关联用户ID
     */
    private UserVO owner;

    /**
     * 操作员ID
     */
    private UserVO operator;

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
