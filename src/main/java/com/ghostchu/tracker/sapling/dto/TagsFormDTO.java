package com.ghostchu.tracker.sapling.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

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
public class TagsFormDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    private Long id;

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 标签名称
     */
    private String tagname;
}
