package com.ghostchu.tracker.sapling.entity.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeerStats implements Serializable {
    private Long seeds;
    private Long leeches;
}