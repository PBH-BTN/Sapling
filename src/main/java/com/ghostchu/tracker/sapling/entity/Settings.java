package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class Settings implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设置记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设置键名
     */
    private String name;

    /**
     * 设置值名
     */
    private String value;
}
