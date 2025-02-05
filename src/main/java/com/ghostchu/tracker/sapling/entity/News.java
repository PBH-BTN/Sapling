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
public class News implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 公告ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String description;

    /**
     * 公告创建用户ID
     */
    private long owner;

    /**
     * 公告创建时间
     */
    private OffsetDateTime createdAt;

    /**
     * 公告到期时间
     */
    private OffsetDateTime expiredAt;

    /**
     * 公告删除时间
     */
    private OffsetDateTime deletedAt;
}
