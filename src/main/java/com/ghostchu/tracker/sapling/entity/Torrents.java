package com.ghostchu.tracker.sapling.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ghostchu.tracker.sapling.converter.JsonbTypeHandler;
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
public class Torrents implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 种子ID
     */
    private Long id;

    /**
     * 种子所有者
     */
    private Long owner;

    /**
     * 种子infohash (v1) sha1
     */
    private byte[] hashV1;

    /**
     * 种子infohash (v2) sha2-256
     */
    private byte[] hashV2;

    /**
     * 种子infohash (v2) sha2-256 (裁断)
     */
    private String hashV2Short;

    /**
     * 隐藏上传者信息
     */
    private Boolean privacy;

    /**
     * 指向的 BitBucket 的原始文件
     */
    private Long file;

    /**
     * 种子主标题
     */
    private String title;

    /**
     * 种子副标题
     */
    private String subtitle;

    /**
     * 种子描述
     */
    private String description;

    /**
     * 种子内容总大小
     */
    private Long size;

    /**
     * 目录ID
     */
    private Long category;

    /**
     * 种子内部文件数量
     */
    private Long numFiles;

    /**
     * 种子创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 删除时间，NULL 为未删除
     */
    private Boolean deletedAt;

    /**
     * 删除操作用户ID
     */
    private Long deletedBy;

    /**
     * 是否公众可见；非公众可见也对 Tracker 不可见
     */
    private Boolean visible;

    /**
     * 扩展信息
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> extra;
}
