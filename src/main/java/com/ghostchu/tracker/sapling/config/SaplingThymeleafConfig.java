package com.ghostchu.tracker.sapling.config;

import com.ghostchu.tracker.sapling.util.TimeConverter;
import com.ghostchu.tracker.sapling.util.UnitConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

@Configuration
public class SaplingThymeleafConfig {

    @Autowired
    private void configureThymeleafStaticVars(ThymeleafViewResolver viewResolver) {
        viewResolver.addStaticVariable("unitConverter", UnitConverter.INSTANCE);
        viewResolver.addStaticVariable("timeConverter", TimeConverter.INSTANCE);
    }
}
