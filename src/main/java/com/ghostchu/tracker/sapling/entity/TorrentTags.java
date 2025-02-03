package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
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
@TableName("torrent_tags")
public class TorrentTags implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 种子-标签 映射关系ID
     */
    private Long id;

    /**
     * 种子ID
     */
    private Long torrent;

    /**
     * 标签ID
     */
    private Long tag;
}
