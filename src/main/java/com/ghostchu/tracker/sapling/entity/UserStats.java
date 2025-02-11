package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("user_stats")
public class UserStats implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private long owner;
    private long uploaded;
    private long downloaded;
    private long uploadedReal;
    private long downloadedReal;
    private long seedTime;
    private long leechTime;
    private long seedScore;

    public float shareRatio() {
        return downloaded == 0 ? -1 : Math.max((float) uploaded / downloaded, 0);
    }
}
