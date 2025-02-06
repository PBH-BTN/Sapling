package com.ghostchu.tracker.sapling.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户 ID
     */
    private long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 头像 URL，可以为相对路径
     */
    private String avatar;

    /**
     * 头衔
     */
    private String title;

    /**
     * 所属权限组 ID
     */
    private PermissionGroupVO primaryPermissionGroup;

    /**
     * BT 上传量
     */
    private long uploaded;

    /**
     * BT 上传量（真实数据）
     */
    private long uploadedReal;

    /**
     * BT 下载量
     */
    private long downloaded;

    /**
     * BT 下载量（真实数据）
     */
    private long downloadedReal;

    /**
     * BT 做种持续时间（毫秒）
     */
    private long seedTime;

    /**
     * BT 下载持续时间（毫秒）
     */
    private long leechTime;

    /**
     * 语言代码
     */
    private String language;

    /**
     * 签名档
     */
    private String signature;

    /**
     * 我的可用上传带宽
     */
    private long myBandwidthUpload;

    /**
     * 我的可用下载带宽
     */
    private long myBandwidthDownload;

    /**
     * 我的网络服务提供商
     */
    private String myIsp;

    /**
     * 账号封禁
     */
    private boolean banned;

    /**
     * 账号警告 ID
     */
    private boolean warned;

    /**
     * 是否为系统账户
     */
    private boolean systemAccount;

    /**
     * 晋级权限组
     */
    private LevelPermissionGroupVO levelPermissionGroup;
}
