package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.ghostchu.tracker.sapling.service.INewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private INewsService newsService;
    @GetMapping("/")
    @SaCheckLogin
    public String index(Model model) {
        var news = newsService.getActiveNews().stream().map(newsService::toVO).toList();
        model.addAttribute("news", news);
        return "index";
    }
}
