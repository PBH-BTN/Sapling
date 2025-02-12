package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ghostchu.tracker.sapling.converter.JsonbTypeHandler;
import com.ghostchu.tracker.sapling.util.bean.TorrentInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Map;

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
@TableName(autoResultMap = true)
public class Torrents implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 种子ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 种子所有者
     */
    private long owner;

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
    private byte[] hashV2Short;

    /**
     * 隐藏上传者信息
     */
    private boolean anonymous;

    /**
     * 指向的 BitBucket 的原始文件
     */
    private long file;

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
    private long size;

    /**
     * 目录ID
     */
    private long category;

    /**
     * 种子内部文件数量
     */
    private long numFiles;

    /**
     * 种子创建时间
     */
    private OffsetDateTime createdAt;

    /**
     * 删除时间，NULL 为未删除
     */
    private OffsetDateTime deletedAt;

    /**
     * 删除操作用户ID
     */
    private Long deletedBy;

    /**
     * 是否公众可见；非公众可见也对 Tracker 不可见
     */
    private boolean visible;

    /**
     * 扩展信息
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private TorrentInfo info;

    /**
     * 扩展信息
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> extra;

    private Long promotion;

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
