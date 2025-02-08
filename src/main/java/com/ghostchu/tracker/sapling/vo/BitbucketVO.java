package com.ghostchu.tracker.sapling.vo;

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
@TableName(autoResultMap = true)
public class BitbucketVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件上传ID
     */
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
     * 文件上传时间
     */
    private OffsetDateTime createdAt;

    /**
     * 文件最后访问时间
     */
    private OffsetDateTime lastAccessAt;

    /**
     * 允许外部直接访问（通过浏览器）
     */
    private boolean directAccess;

    /**
     * 文件大小 (bytes)
     */
    private long fileSize;
    /**
     * MIME 类型
     */
    private String mime;
}
