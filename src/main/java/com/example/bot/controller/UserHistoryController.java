package com.example.bot.controller;

import com.example.bot.entity.ChatSession;
import com.example.bot.entity.Message;
import com.example.bot.entity.User;
import com.example.bot.repository.ChatSessionRepository;
import com.example.bot.repository.MessageRepository;
import com.example.bot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class UserHistoryController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users-list";
    }

    @GetMapping("/users/{userId}/history")
    public String showUserHistory(@PathVariable Long userId, Model model) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + userId));

        ChatSession session = chatSessionRepository.findByUserId(userId)
            .orElse(null);

        List<Message> messages = session != null
            ? messageRepository.findBySessionIdOrderByTimestamp(session.getId())
            : List.of();

        // Преобразуем список сообщений для удобного отображения
        List<HistoryItem> historyItems = messages.stream()
            .map(msg -> new HistoryItem(
                msg.getRole().equals("user") ? "Пользователь" : "Бот",
                msg.getContent(),
                msg.getTimestamp(),
                session != null ? session.getModelName() : "—"
            ))
            .collect(Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("historyItems", historyItems);
        model.addAttribute("currentModel", session != null ? session.getModelName() : "—");
        return "user-history";
    }

    // Вспомогательный класс для шаблона
    public static class HistoryItem {
        private final String sender;
        private final String content;
        private final java.time.LocalDateTime timestamp;
        private final String model;

        public HistoryItem(String sender, String content, java.time.LocalDateTime timestamp, String model) {
            this.sender = sender;
            this.content = content;
            this.timestamp = timestamp;
            this.model = model;
        }

        public String getSender() { return sender; }
        public String getContent() { return content; }
        public java.time.LocalDateTime getTimestamp() { return timestamp; }
        public String getModel() { return model; }
    }
}