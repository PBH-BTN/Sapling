package com.ghostchu.tracker.sapling.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.net.InetAddress;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
public class PeersVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private TorrentVO torrent;
    private UserVO owner;
    private byte[] peerId;
    private InetAddress ip;
    private int port;
    private long uploaded;
    private long downloaded;
    // 这就是 left，但是 left 是关键字
    private long toGo;
    private OffsetDateTime started;
    private OffsetDateTime lastAnnounce;
    private Boolean connectable;
    private long downloadOffset;
    private long uploadOffset;
    private String userAgent;
    private String lastAction;
    private InetAddress reqIp;
}
