package com.ghostchu.tracker.sapling.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ghostchu.tracker.sapling.converter.JsonbTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@ToString
public class TorrentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 种子ID
     */
    private long id;

    /**
     * 种子所有者
     */
    private long owner;

    private String ownerName;

    /**
     * 种子infohash (v1) sha1
     */
    private String hashV1;

    /**
     * 种子infohash (v2) sha2-256
     */
    private String hashV2;

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
     * 分类
     */
    private long category;

    private String categoryName;

    /**
     * 种子内部文件数量
     */
    private long numFiles;

    /**
     * 种子创建时间
     */
    private OffsetDateTime createdAt;

    /**
     * 扩展信息
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> extra;
}
