package com.ghostchu.tracker.sapling.model;

import com.ghostchu.tracker.sapling.tracker.PeerEvent;

import java.net.InetAddress;

public record AnnounceRequest(long torrentId, long userId, byte[] peerId, InetAddress reqIpInetAddress,
                              InetAddress peerIp, int port, long uploaded, long downloaded, long left,
                              PeerEvent peerEvent, String ua) {
}
