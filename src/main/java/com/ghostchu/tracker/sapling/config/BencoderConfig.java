package com.ghostchu.tracker.sapling.config;

import com.dampcake.bencode.Bencode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class BencoderConfig {
    @Bean
    public Bencode bencodeISO8859() {
        return new Bencode(StandardCharsets.ISO_8859_1);
    }

    @Bean
    @Qualifier("bencodeUTF8")
    public Bencode bencodeUTF8() {
        return new Bencode(StandardCharsets.UTF_8);
    }
}
