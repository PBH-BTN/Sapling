package com.ghostchu.tracker.sapling.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
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
public class Reports implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报告ID
     */
    private Long id;

    /**
     * 类型
     */
    private Short type;

    /**
     * 目标类型的 ID 值
     */
    private Long targetId;

    /**
     * 报告所属用户ID
     */
    private Long owner;

    /**
     * 报告备注
     */
    private String comment;

    /**
     * 报告创建时间
     */
    private LocalDateTime reportedAt;

    /**
     * 报告解决时间
     */
    private LocalDateTime resolvedAt;

    /**
     * 报告处理用户ID
     */
    private Long resolvedBy;
}
