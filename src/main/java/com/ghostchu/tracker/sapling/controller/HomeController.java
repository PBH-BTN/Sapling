package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    @SaCheckLogin
    public String index() {
        return "index";
    }
}
