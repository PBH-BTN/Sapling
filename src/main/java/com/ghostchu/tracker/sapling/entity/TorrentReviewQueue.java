package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

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
@TableName("torrent_review_queue")
public class TorrentReviewQueue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 种子待审队列
     */
    private Long id;

    /**
     * 种子ID
     */
    private Long torrent;

    /**
     * 种子提交时间
     */
    private OffsetDateTime submitAt;
}
