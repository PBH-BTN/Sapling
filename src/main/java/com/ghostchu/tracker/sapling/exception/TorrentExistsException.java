package com.ghostchu.tracker.sapling.exception;

public class TorrentExistsException extends BusinessException {
    private final byte[] infoHash;

    public TorrentExistsException(byte[] infoHash, String message) {
        super(message);
        this.infoHash = infoHash;
    }
}
