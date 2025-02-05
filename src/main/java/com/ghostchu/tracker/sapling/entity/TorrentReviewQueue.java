package com.ghostchu.tracker.sapling.entity;

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
@TableName("torrent_review_queue")
public class TorrentReviewQueue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 种子待审队列
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 种子ID
     */
    private long torrent;

    /**
     * 种子提交时间
     */
    private OffsetDateTime submitAt;
}
