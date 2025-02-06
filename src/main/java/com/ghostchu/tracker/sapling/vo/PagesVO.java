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

    private String htmlContent;

    private OffsetDateTime createdAt;

    private OffsetDateTime editedAt;
}
