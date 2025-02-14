package com.ghostchu.tracker.sapling.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableId(type = IdType.AUTO)
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
    private UserVO owner;

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
}
