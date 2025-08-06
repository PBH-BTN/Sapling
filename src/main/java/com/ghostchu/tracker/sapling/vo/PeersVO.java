package com.ghostchu.tracker.sapling.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
public class PeersVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private TorrentsVO torrent;
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

    public String ipType() {
        if (ip instanceof Inet4Address) {
            return "IPv4";
        } else if (ip instanceof Inet6Address) {
            return "IPv6";
        } else {
            return "Unknown";
        }
    }


    public float shareRatio() {
        return downloaded == 0 ? -1 : Math.max((float) uploaded / downloaded, 0);
    }

    public String shareRatioStr() {
        String shareRatioString;
        if (shareRatio() == -1) {
            shareRatioString = "Inf.";
        } else {
            shareRatioString = String.format("%.2f", shareRatio());
        }
        return shareRatioString;
    }

    public String progress() {
        return String.format("%.2f", (1 - (float) toGo / torrent.getSize()) * 100);
    }

    public long connectionTime() {
        return (OffsetDateTime.now().toEpochSecond() - started.toEpochSecond()) * 1000;
    }
}
