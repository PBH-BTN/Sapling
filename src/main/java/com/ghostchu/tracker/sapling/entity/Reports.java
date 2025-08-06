package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.OffsetDateTime;

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
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 类型
     */
    private short type;

    /**
     * 目标类型的 ID 值
     */
    private long targetId;

    /**
     * 报告所属用户ID
     */
    private long owner;

    /**
     * 报告备注
     */
    private String comment;

    /**
     * 报告创建时间
     */
    private OffsetDateTime reportedAt;

    /**
     * 报告解决时间
     */
    private OffsetDateTime resolvedAt;

    /**
     * 报告处理用户ID
     */
    private Long resolvedBy;
}
