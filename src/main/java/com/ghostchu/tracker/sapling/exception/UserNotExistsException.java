package com.ghostchu.tracker.sapling.exception;

import java.util.Optional;

public class UserNotExistsException extends BusinessException {
    private final Long id;

    public UserNotExistsException(Long id, String message) {
        super(message);
        this.id = id;
    }

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }
}
