package com.example.bot.controller;

import com.example.bot.entity.User;
import com.example.bot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("newUserAction", new UserActionForm());
        return "userslist";
    }

    // Форма для блокировки/тарифа
    public static class UserActionForm {
        private Long userId;
        private String action; // "block_temp", "block_permanent", "unblock", "premium"
        private Integer days;  // для временной блокировки или тарифа

        // геттеры и сеттеры
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public Integer getDays() { return days; }
        public void setDays(Integer days) { this.days = days; }
    }

    @PostMapping("/admin/users/action")
    public String performUserAction(@ModelAttribute UserActionForm form) {
        User user = userRepository.findById(form.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        switch (form.getAction()) {
            case "block_temp":
                if (form.getDays() != null && form.getDays() > 0) {
                    user.setBlocked(true);
                    user.setBlockedUntil(LocalDateTime.now().plusDays(form.getDays()));
                }
                break;
            case "block_permanent":
                user.setBlocked(true);
                user.setBlockedUntil(null); // навсегда
                break;
            case "unblock":
                user.setBlocked(false);
                user.setBlockedUntil(null);
                break;
            case "premium":
                if (form.getDays() != null && form.getDays() > 0) {
                    user.setPremiumActive(true);
                    user.setPremiumUntil(LocalDateTime.now().plusDays(form.getDays()));
                }
                break;
            case "premium_off":
                user.setPremiumActive(false);
                user.setPremiumUntil(null);
                break;
        }

        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/{userId}/tariff")
    public String showTariffPage(@PathVariable Long userId, Model model) {
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        model.addAttribute("user", user);
        return "tariffi";
    }

    @PostMapping("/users/{userId}/tariff")
    public String activateTariff(@PathVariable Long userId, @RequestParam String tariff) {
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        int days = switch (tariff) {
            case "lite" -> 30;
            case "pro" -> 180;
            case "profi" -> 365;
            default -> throw new IllegalArgumentException("Неизвестный тариф: " + tariff);
        };

        user.setPremiumActive(true);
        user.setPremiumUntil(LocalDateTime.now().plusDays(days));
        userRepository.save(user);

        return "redirect:/admin/users/" + userId + "/history?tariff=success";
    }
}