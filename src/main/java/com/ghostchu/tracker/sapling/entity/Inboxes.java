package com.ghostchu.tracker.sapling.entity;

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
public class Inboxes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 站内信ID
     */
    private Long id;

    /**
     * 收件用户ID
     */
    private Long owner;

    /**
     * 发件用户ID
     */
    private Long sender;

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
    private LocalDateTime createdAt;

    /**
     * 阅读时间，NULL 为未读
     */
    private LocalDateTime readedAt;

    /**
     * 删除时间，NULL 为未删除
     */
    private LocalDateTime deletedAt;

    /**
     * 标记为保存，不会自动删除
     */
    private Boolean saved;
}
