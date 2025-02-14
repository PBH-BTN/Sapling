package com.ghostchu.tracker.sapling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TrackerSettingDTO {
    private String announceUrl;
    private long announceIntervalMillis;
    private long announceIntervalRandomOffsetMillis;
    private boolean maintenance;
    private String maintenanceMessage;
    private int announceMaxReturns;
    private String trackerId;
}
