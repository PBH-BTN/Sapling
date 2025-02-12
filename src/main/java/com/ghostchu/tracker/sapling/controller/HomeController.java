package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckLogin;
import com.ghostchu.tracker.sapling.service.INewsService;
import com.ghostchu.tracker.sapling.util.ProcessorInformationCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import oshi.SystemInfo;

@Controller
@SaCheckDisable
public class HomeController {
    private static final SystemInfo systemInfo = new SystemInfo();
    @Autowired
    private INewsService newsService;
    @Autowired
    private ProcessorInformationCollector processorInformationCollector;

    @GetMapping("/")
    @SaCheckLogin
    public String index(Model model) {
        var news = newsService.getActiveNews().stream().map(newsService::toVO).toList();
        model.addAttribute("news", news);
        model.addAttribute("cpuUsage", processorInformationCollector.getCpuUsage());
        model.addAttribute("sysLoads", processorInformationCollector.getSysLoads());
        return "index";
    }
}
