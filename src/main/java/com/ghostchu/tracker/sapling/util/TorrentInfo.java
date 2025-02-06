package com.ghostchu.tracker.sapling.util;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;

// 数据结构定义
public record TorrentInfo(List<String> trackers, String createdBy, OffsetDateTime creationDate, String encoding,
                          TorrentNode fileTree, boolean isPrivate, String publisher, String comment,
                          int metaVersion, boolean hybrid,
                          String infoHashV1, String infoHashV2,
                          long totalSize,
                          long files) implements Serializable {

    private static final long serialVersionUID = 1L;

    public byte[] infoHashV1Bytes() {
        return infoHashV1 != null ? HexFormat.of().parseHex(infoHashV1) : null;
    }

    public byte[] infoHashV2Bytes() {
        return infoHashV2 != null ? HexFormat.of().parseHex(infoHashV2) : null;
    }
    // 构造函数、getter省略
}
