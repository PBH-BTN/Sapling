package com.ghostchu.tracker.sapling.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableId(type = IdType.AUTO)
    private Long id;
    private long torrent;
    private byte[] peerId;
    @TableField(typeHandler = InetAddressTypeHandler.class)
    private InetAddress ip;
    private int port;
    private long uploaded;
    private long downloaded;
    private long toGo;
    private OffsetDateTime started;
    private OffsetDateTime lastAnnounce;
    private Boolean connectable;
    private long downloadOffset;
    private long uploadOffset;
    private String userAgent;
    private String lastAction;

    /**
     * 扩展信息
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> extra;
}
