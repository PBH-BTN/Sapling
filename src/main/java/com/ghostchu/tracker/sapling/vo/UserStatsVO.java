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
public class UserStatsVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private long owner;
    private long uploaded;
    private long downloaded;
    private long uploadedReal;
    private long downloadedReal;
    private long seedTime;
    private long leechTime;
    private long seedScore;
    private float shareRatio;
    private String shareRatioStr;
}
