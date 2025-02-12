package com.ghostchu.tracker.sapling.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class CommentsVO implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private UserVO owner;

    private TorrentsVO torrent;

    private String content;

    private CommentsVO parentComment; // 楼中楼

    private OffsetDateTime createdAt;

    private OffsetDateTime editedAt;

    private OffsetDateTime deletedAt;
}
