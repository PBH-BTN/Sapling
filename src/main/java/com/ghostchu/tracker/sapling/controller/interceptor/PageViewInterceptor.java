package com.ghostchu.tracker.sapling.controller.interceptor;

import com.ghostchu.tracker.sapling.event.UserViewPageEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

;

@Component
public class PageViewInterceptor implements HandlerInterceptor {

    private final ApplicationEventPublisher eventPublisher;

    // 通过构造器注入 ApplicationEventPublisher
    public PageViewInterceptor(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        UserViewPageEvent event = new UserViewPageEvent(this);
        eventPublisher.publishEvent(event);
        if (modelAndView == null) return;
        modelAndView.getModelMap().addAttribute("siteEvents", event.getSiteEventDTOList());
    }
}