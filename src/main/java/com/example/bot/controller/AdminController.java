package com.example.bot.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.bot.entity.User;
import com.example.bot.repository.UserRepository;
import com.example.bot.service.ChatHistoryService;

@Controller
public class AdminController {

    
    private final ChatHistoryService chatHistoryService;

    public AdminController(UserRepository userRepository, ChatHistoryService chatHistoryService) {    
        this.chatHistoryService = chatHistoryService;
    }

    @GetMapping("/admin")
    public String index() {
        return "admin";
    }

    /*
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
        */

    /*
    @PostMapping("/users/{userId}/model")
    public void changeModel(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        String model = payload.get("model");
        if (model != null && !model.trim().isEmpty()) {
            chatHistoryService.updateModel(userId, model.trim());
        }
    }
    */

    /*
    @PostMapping("/users/send")
    public void sendAdminMessage(@RequestBody Map<String, Object> payload) {
        // TODO: реализовать отправку через TelegramBotsApi
        // Например, внедрить MyTelegramBot или TelegramApiClient
    }
        */
}