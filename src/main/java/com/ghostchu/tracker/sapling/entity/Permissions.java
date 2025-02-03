package com.ghostchu.tracker.sapling.entity;

import java.io.Serializable;
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
public class Permissions implements Serializable {

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
    private Integer targetGroup;
}
