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
public class MessagesVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 站内信ID
     */
    private Long id;

    /**
     * 收件用户ID
     */
    private UserVO owner;

    /**
     * 发件用户ID
     */
    private UserVO sender;

    /**
     * 信件标题
     */
    private String title;

    /**
     * 信件内容
     */
    private String description;

    /**
     * 发件时间
     */
    private OffsetDateTime createdAt;

    /**
     * 阅读时间，NULL 为未读
     */
    private OffsetDateTime readAt;

    /**
     * 删除时间，NULL 为未删除
     */
    private OffsetDateTime deletedAt;

}
