package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ghostchu.tracker.sapling.converter.JsonbTypeHandler;
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
public class Bitbucket implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件上传ID
     */
    @TableId(type = IdType.AUTO)
    @OrderBy()
    private Long id;

    /**
     * 文件显示名称
     */
    private String displayName;

    /**
     * 文件存储位置
     */
    private String filePath;

    /**
     * 路由处理程序
     */
    private String handler;

    /**
     * 文件所属人
     */
    private long owner;

    /**
     * 文件上传时间
     */
    private OffsetDateTime createdAt;

    /**
     * 文件最后访问时间
     */
    private OffsetDateTime lastAccessAt;

    /**
     * 删除时间，NULL 为未删除
     */
    private OffsetDateTime deletedAt;

    /**
     * 允许外部直接访问（通过浏览器）
     */
    private boolean directAccess;

    /**
     * 文件大小
     */
    private long fileSize;

    private String mime;

    private boolean managed;

    /**
     * 扩展信息
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> extra;
}
