package com.ghostchu.tracker.sapling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiteEventDTO {
    private String title;
    private String description;
    private String alertType;
}
