package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

import com.ghostchu.tracker.sapling.converter.InetAddressTypeHandler;
import com.ghostchu.tracker.sapling.converter.JsonbTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Getter
@Setter
@ToString
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 用户密码哈希
     */
    private String passhash;

    /**
     * 用户电子邮件地址
     */
    private String email;

    /**
     * 用户注册时间
     */
    private OffsetDateTime registerAt;

    /**
     * 用户上次登录时间
     */
    private OffsetDateTime lastLoginAt;

    /**
     * 用户上次访问时间
     */
    private OffsetDateTime lastAccessAt;

    /**
     * 注册 IP 地址
     */
    @TableField(typeHandler = InetAddressTypeHandler.class)
    private InetAddress registerIp;

    /**
     * 上次登录 IP 地址
     */
    @TableField(typeHandler = InetAddressTypeHandler.class)
    private InetAddress lastLoginIp;

    /**
     * 上次访问 IP 地址
     */
    @TableField(typeHandler = InetAddressTypeHandler.class)
    private InetAddress lastAccessIp;

    /**
     * passkey 密钥
     */
    private String passkey;

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
    private Long primaryPermissionGroup;

    /**
     * BT 上传量
     */
    private Long uploaded;

    /**
     * BT 上传量（真实数据）
     */
    private Long uploadedReal;

    /**
     * BT 下载量
     */
    private Long downloaded;

    /**
     * BT 下载量（真实数据）
     */
    private Long downloadedReal;

    /**
     * BT 做种持续时间（毫秒）
     */
    private Long seedTime;

    /**
     * BT 下载持续时间（毫秒）
     */
    private Long leechTime;

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
    private Long myBandwidthUpload;

    /**
     * 我的可用下载带宽
     */
    private Long myBandwidthDownload;

    /**
     * 我的网络服务提供商
     */
    private String myIsp;

    /**
     * 账号暂停状态
     */
    private Boolean parked;

    /**
     * 账号封禁 ID，指向一条封禁记录，NULL 则未封禁
     */
    private Long bannedId;

    /**
     * 账号警告 ID，指向一条警告记录，NULL 则未封禁，需要额外检查警告有效期
     */
    private Long warnedId;

    /**
     * 扩展数据
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> extra;
    /**
     * 是否为系统账户
     */
    private Boolean systemAccount;
    /**
     * 是否允许登录会话
     */
    private Boolean allowLogin;
    /**
     * 晋级权限组
     */
    private Long levelPermissionGroup;
}
