package com.ghostchu.tracker.sapling;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Paths;

public class TestEntrypoint {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:postgresql://nahida:5432/sapling", "sapling", "N7tzj6857pwWWcFA")
                .globalConfig(builder -> builder
                        .author("Ghost_chu")
                        .outputDir("F:\\Sapling\\src\\main\\java")
                        .commentDate("yyyy-MM-dd")
                )
                .packageConfig(builder -> builder
                        .parent("com.ghostchu.tracker.sapling")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .xml("mapper.xml")
                )
                .strategyConfig(builder -> builder
                        .entityBuilder()
                        .enableLombok()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
