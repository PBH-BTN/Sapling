package com.ghostchu.tracker.sapling.vo;

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
public class ThanksVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 说谢谢记录ID
     */
    private long id;

    /**
     * 用户ID
     */
    private UserVO owner;

    /**
     * 种子ID
     */
    private TorrentVO torrent;

    private OffsetDateTime createAt;
}
