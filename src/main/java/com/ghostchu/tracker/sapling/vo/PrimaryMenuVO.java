package com.ghostchu.tracker.sapling.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class PrimaryMenuVO {
    private Long id;
    private String name;
    private List<SubMenuVO> subMenus;
}