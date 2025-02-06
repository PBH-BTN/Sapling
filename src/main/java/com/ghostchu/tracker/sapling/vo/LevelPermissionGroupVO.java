package com.ghostchu.tracker.sapling.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LevelPermissionGroupVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限组 ID
     */
    private long id;

    /**
     * 权限组名称
     */
    private String name;

    /**
     * 权限组颜色 #hex
     */
    private String color;
}
