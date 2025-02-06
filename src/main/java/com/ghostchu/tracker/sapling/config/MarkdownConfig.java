package com.ghostchu.tracker.sapling.config;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MarkdownConfig {
    private final List<Extension> extensions = List.of(TablesExtension.create());

    @Bean
    public HtmlRenderer htmlRenderer() {
        return HtmlRenderer.builder()
                .extensions(extensions)
                .build();
    }

    @Bean
    public Parser parser() {
        return Parser.builder()
                .extensions(extensions)
                .build();
    }
}
