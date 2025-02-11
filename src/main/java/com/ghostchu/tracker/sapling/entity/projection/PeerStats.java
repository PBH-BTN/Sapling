package com.ghostchu.tracker.sapling.entity.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeerStats {
    private Long seeds;
    private Long leeches;
}