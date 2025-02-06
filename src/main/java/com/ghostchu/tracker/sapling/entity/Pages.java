package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
public class Pages implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String slug;

    private String title;

    private String content;

    private String type;

    private OffsetDateTime createdAt;

    private OffsetDateTime editedAt;

    private OffsetDateTime deletedAt;

    private Long createdBy;

    private Long editedBy;

    private Long deletedBy;
}
