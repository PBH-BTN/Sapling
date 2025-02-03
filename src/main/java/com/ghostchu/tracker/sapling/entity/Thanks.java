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
public class Thanks implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 说谢谢记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long owner;

    /**
     * 种子ID
     */
    private Long torrent;
}
