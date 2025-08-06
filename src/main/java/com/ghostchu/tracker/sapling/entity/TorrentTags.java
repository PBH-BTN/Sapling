package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ghostchu.tracker.sapling.converter.JsonbTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
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
@TableName("torrent_tags")
public class TorrentTags implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 种子-标签 映射关系ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 种子ID
     */
    private long torrent;

    /**
     * 标签ID
     */
    private long tag;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> extra;
}
