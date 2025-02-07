package com.ghostchu.tracker.sapling.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

// 请求体DTO
@Data
@NoArgsConstructor
public class CommentFormDTO {
    @NotBlank
    @Size(max = 2000)
    private String content;
}