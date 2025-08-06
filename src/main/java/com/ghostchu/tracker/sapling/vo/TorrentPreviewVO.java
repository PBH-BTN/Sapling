package com.ghostchu.tracker.sapling.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TorrentPreviewVO {
    private long torrentId;
    private long comments;
    private long seeds;
    private long leeches;
}
