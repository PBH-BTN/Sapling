package com.ghostchu.tracker.sapling.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
public class TorrentVO implements Serializable {

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

    private boolean anonymous;
}
