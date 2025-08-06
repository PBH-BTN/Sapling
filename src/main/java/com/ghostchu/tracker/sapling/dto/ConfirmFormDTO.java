package com.ghostchu.tracker.sapling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmFormDTO {
    private String actionUrl;
    private String headerClass = "bg-warning";
    private String titleIconClass = "fas fa-question";
    private String title = "操作需要确认";
    private String description = "此操作未提供描述";
    private String confirmButtonText = "确认";
    private String confirmButtonClass = "fas fa-check";
    private String cancelButtonText = "取消";
    private String cancelButtonClass = "fas fa-xmark";
    private String cancelButtonHref = "javascript:history.go(-1)";
}
