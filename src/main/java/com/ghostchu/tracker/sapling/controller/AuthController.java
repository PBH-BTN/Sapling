package com.ghostchu.tracker.sapling.controller;

import com.ghostchu.tracker.sapling.dto.RegForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("username", "1212");
        model.addAttribute("password", "121212");
        return "login";
    }
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerForm", new RegForm());
        return "register";
    }
}
