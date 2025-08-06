package com.ghostchu.tracker.sapling.vo;

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
public class PermissionsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限分配ID
     */
    private Long id;

    /**
     * 权限节点
     */
    private String node;

    /**
     * 所属权限组
     */
    private long targetGroup;

    /**
     * 指定的所属权限组 ID 是否为等级权限组的 ID
     */
    private boolean targetIsLevelGroup;
}
