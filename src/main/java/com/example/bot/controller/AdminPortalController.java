// src/main/java/com/example/bot/controller/AdminPortalController.java
package com.example.bot.controller;

import com.example.bot.repository.UserRepository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPortalController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String adminPortal(Model model) {
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("todayUsers", userRepository.countToday());
        model.addAttribute("last7DaysUsers", userRepository.countLast7Days(LocalDateTime.now()));
        return "admins";
    }

    @GetMapping("/admin/")
    public String adminPortalRoot() {
        return "redirect:/admin";
    }
}