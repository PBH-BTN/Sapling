package com.ghostchu.tracker.sapling.controller.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 全局异常拦截
    @ExceptionHandler
    public String handlerException(Exception e, Model model) {
        model.addAttribute("err", e.getMessage());
        log.error("未处理的全局异常: {}", e.getMessage(), e);
        return "error";
    }
}
