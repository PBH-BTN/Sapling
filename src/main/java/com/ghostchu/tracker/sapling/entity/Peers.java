package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ghostchu.tracker.sapling.converter.InetAddressTypeHandler;
import com.ghostchu.tracker.sapling.converter.JsonbTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.net.InetAddress;
import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@ToString
public class Peers implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long torrent;
    private Long peerId;
    @TableField(typeHandler = InetAddressTypeHandler.class)
    private InetAddress ip;
    private Integer port;
    private Long uploaded;
    private Long downloaded;
    private Long toGo;
    private OffsetDateTime started;
    private OffsetDateTime lastAnnounce;
    private Boolean connectable;
    private Long downloadOffset;
    private Long uploadOffset;
    private String userAgent;
    private String lastAction;

    /**
     * 扩展信息
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> extra;
}
