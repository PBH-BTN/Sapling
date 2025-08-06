package com.ghostchu.tracker.sapling.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
public class PagesVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String slug;

    private String title;

    private String content;

    private String type;
    private OffsetDateTime createdAt;
    // editedAt 可能为 null，null 代表从未编辑过
    private OffsetDateTime editedAt;
}
