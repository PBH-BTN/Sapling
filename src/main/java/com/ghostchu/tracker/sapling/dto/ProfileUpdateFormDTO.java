package com.ghostchu.tracker.sapling.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProfileUpdateFormDTO {

    private long id;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名格式不正确")
    private String name;

    @Size(max = 50)
    private String title;

    @Size(max = 200)
    private String signature;

    private String avatar;

    @Min(1)
    @Max(1000)
    private long myBandwidthUpload;

    @Min(1)
    @Max(1000)
    private long myBandwidthDownload;

    @Size(max = 50)
    private String myIsp;

    private long primaryGroupId;

    private long uploaded;
    private long uploadedReal;
    private long downloaded;
    private long downloadedReal;
    private long seedTime;
    private long leechTime;


}