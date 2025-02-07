package com.ghostchu.tracker.sapling.exception;

import java.util.Optional;

public class UsernameUnavailableException extends BusinessException {
    private final Long id;
    private final String username;

    public UsernameUnavailableException(Long id, String username, String message) {
        super(message);
        this.username = username;
        this.id = id;
    }

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }
}
