package com.ghostchu.tracker.sapling.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProfileUpdateFormDTO {

    private long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度需在3-20个字符之间")
    private String name;

    @Size(max = 50)
    private String title;

    @Size(max = 200)
    private String signature;

    private String avatar;

    @Min(0)
    @Max(1000)
    private long myBandwidthUpload;

    @Min(0)
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