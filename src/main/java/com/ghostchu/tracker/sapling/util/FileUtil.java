package com.ghostchu.tracker.sapling.util;

import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.util.MimeType;

import java.util.Optional;

public class FileUtil {
    public static MimeType mime(String fileName, MediaType defaultMime) {
        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(fileName);
        return mediaType.orElse(defaultMime);
    }
}
