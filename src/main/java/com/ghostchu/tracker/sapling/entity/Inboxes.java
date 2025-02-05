package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class Inboxes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 站内信ID
     */
    @TableId(type = IdType.AUTO)
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
    private OffsetDateTime createdAt;

    /**
     * 阅读时间，NULL 为未读
     */
    private OffsetDateTime readedAt;

    /**
     * 删除时间，NULL 为未删除
     */
    private OffsetDateTime deletedAt;

    /**
     * 标记为保存，不会自动删除
     */
    private Boolean saved;
}
