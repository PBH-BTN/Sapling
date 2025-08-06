package com.ghostchu.tracker.sapling.exception;

import java.util.Optional;

public class TorrentNotExistsException extends BusinessException {
    private final String infoHash;
    private final Long id;

    public TorrentNotExistsException(String infoHash, Long id, String message) {
        super(message);
        this.infoHash = infoHash;
        this.id = id;
    }

    public Optional<String> getInfoHash() {
        return Optional.ofNullable(infoHash);
    }

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }
}
