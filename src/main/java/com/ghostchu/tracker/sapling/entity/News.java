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
public class News implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 公告ID
     */
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
    private Long owner;

    /**
     * 公告创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 公告到期时间
     */
    private LocalDateTime expiredAt;

    /**
     * 公告删除时间
     */
    private LocalDateTime deletedAt;
}
