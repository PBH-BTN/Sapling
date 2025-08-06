package com.ghostchu.tracker.sapling.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TorrentEditFormDTO {
    @NotNull
    private Long categoryId;

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    @Size(max = 100, message = "副标题长度不能超过100个字符")
    private String subtitle;

    private String description;
    private boolean anonymous;
    private String tags;
    private Long promotion;
}
