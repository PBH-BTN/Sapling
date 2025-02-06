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
public class CommentVO implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private long owner;

    private long torrent;

    private CommentVO parentComment; // 楼中楼

    private OffsetDateTime createdAt;

    private OffsetDateTime editedAt;

    private OffsetDateTime deletedAt;

    private UserVO deletedBy;
}
