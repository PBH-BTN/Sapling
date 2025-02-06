package com.ghostchu.tracker.sapling.model;

import java.io.Serializable;

public record StdMsg<T>(boolean success, String message, T data) implements Serializable {
    private static final long serialVersionUID = 1L;
}
