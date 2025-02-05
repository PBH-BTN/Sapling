package com.ghostchu.tracker.sapling.model;

public record StdMsg<T>(boolean success, String message, T data) {
}
