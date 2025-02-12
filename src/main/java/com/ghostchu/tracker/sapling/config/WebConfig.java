package com.ghostchu.tracker.sapling.config;

import com.ghostchu.tracker.sapling.controller.interceptor.PageViewInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final PageViewInterceptor pageViewInterceptor;

    // 构造器注入拦截器
    public WebConfig(PageViewInterceptor pageViewInterceptor) {
        this.pageViewInterceptor = pageViewInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(pageViewInterceptor)
                .addPathPatterns("/**")          // 拦截所有路径
                .excludePathPatterns("/static/**"); // 排除静态资源（可选）
    }
}