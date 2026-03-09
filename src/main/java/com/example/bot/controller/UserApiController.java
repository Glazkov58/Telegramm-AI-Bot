package com.example.bot.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bot.entity.User;
import com.example.bot.repository.UserRepository;
import com.example.bot.service.ChatHistoryService;


/*@RestController
@RequestMapping("/api")
public class UserApiController {
    @Autowired
    private ChatHistoryService chatHistoryService;
    private UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
        // Реализуйте через UserRepository
    }

    @PostMapping("/users/{userId}/model")
    public void changeModel(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        chatHistoryService.updateModel(userId, payload.get("model"));
    }

    @PostMapping("/send")
    public void sendAdminMessage(@RequestBody Map<String, Object> payload) {
        // Отправка через TelegramBotsApi
    }
}
*/