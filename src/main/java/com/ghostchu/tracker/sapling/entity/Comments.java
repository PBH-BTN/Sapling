package com.ghostchu.tracker.sapling.entity;

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
public class Comments implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long owner;

    private Long torrent;

    private Long parentComment;

    private OffsetDateTime createdAt;

    private OffsetDateTime editedAt;

    private OffsetDateTime deletedAt;

    private Long deletedBy;
}
