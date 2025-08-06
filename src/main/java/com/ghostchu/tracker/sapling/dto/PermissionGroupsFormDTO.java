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
public class PermissionGroupsFormDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限组 ID
     */
    private Long id;

    /**
     * 权限组名称
     */
    private String name;

    /**
     * 权限组优先级
     */
    private int priority;

    /**
     * 权限组颜色 #hex
     */
    private String color;
}
