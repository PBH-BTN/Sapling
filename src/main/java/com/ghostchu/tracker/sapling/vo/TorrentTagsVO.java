package com.ghostchu.tracker.sapling.vo;

import com.baomidou.mybatisplus.annotation.TableField;
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
public class TorrentTagsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 种子-标签 映射关系ID
     */
    private Long id;

    /**
     * 种子ID
     */
    private TorrentsVO torrent;

    /**
     * 标签ID
     */
    private TagsVO tag;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> extra;
}
