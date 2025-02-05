package com.ghostchu.tracker.sapling.vo;

import com.ghostchu.tracker.sapling.util.TorrentParser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
public class TorrentDetailsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 种子ID
     */
    private long id;

    /**
     * 种子所有者
     */
    private UserVO owner;

    /**
     * 种子主标题
     */
    private String title;

    /**
     * 种子副标题
     */
    private String subtitle;

    /**
     * 种子描述
     */
    private String description;

    /**
     * 种子内容总大小
     */
    private long size;

    /**
     * 分类
     */
    private CategoryVO category;

    /**
     * 种子内部文件数量
     */
    private long numFiles;

    /**
     * 种子创建时间
     */
    private OffsetDateTime createdAt;

    private TorrentParser.TorrentInfo info;

    private boolean anonymous;

}
