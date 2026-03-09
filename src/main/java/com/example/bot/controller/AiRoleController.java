package com.example.bot.controller;

import com.example.bot.entity.AiRole;
import com.example.bot.service.AiRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AiRoleController {

    @Autowired
    private AiRoleService aiRoleService;

    private static final List<String> PREDEFINED_ROLES = Arrays.asList(
        "Автотематика", "Программирование", "Психолог", "Бизнес", "Быт", "Ремонт", "Советы"
    );

    @GetMapping("/roles")
    public String listRoles(Model model) {
        // Создаём предопределённые роли, если их нет
        for (String roleName : PREDEFINED_ROLES) {
            if (!aiRoleService.existsByTitle(roleName)) {
                AiRole role = new AiRole();
                role.setTitle(roleName);
                role.setPrompt(getDefaultPrompt(roleName));
                role.setActive(true); // по умолчанию активны
                aiRoleService.save(role);
            }
        }
        model.addAttribute("roles", aiRoleService.findAll());
        model.addAttribute("newRole", new AiRole());
        return "airoles";
    }

    @PostMapping("/roles")
    public String addRole(@ModelAttribute("newRole") AiRole newRole) {
        if (!aiRoleService.existsByTitle(newRole.getTitle())) {
            if (newRole.getPrompt() == null || newRole.getPrompt().isBlank()) {
                newRole.setPrompt(getDefaultPrompt(newRole.getTitle()));
            }
            newRole.setActive(true); // новая роль — активна по умолчанию
            aiRoleService.save(newRole);
        }
        return "redirect:/admin/roles";
    }

    @PostMapping("/roles/{id}/toggle")
    public String toggleActive(@PathVariable Long id) {
        AiRole role = aiRoleService.findById(id);
        if (role != null) {
            role.setActive(!role.isActive());
            aiRoleService.save(role);
        }
        return "redirect:/admin/roles";
    }

    @PostMapping("/roles/{id}/delete")
    public String deleteRole(@PathVariable Long id) {
        aiRoleService.deleteById(id);
        return "redirect:/admin/roles";
    }

    private String getDefaultPrompt(String roleName) {
        return switch (roleName) {
            case "Автотематика" -> "Вы — эксперт по автомобилям. Отвечайте на вопросы по ремонту, диагностике, выбору авто и автосервисам.";
            case "Программирование" -> "Вы — опытный программист. Помогайте с кодом, архитектурой, отладкой и лучшими практиками.";
            case "Психолог" -> "Вы — дружелюбный психолог. Давайте поддержку, советы по эмоциям и взаимоотношениям, но не ставьте диагнозов.";
            case "Бизнес" -> "Вы — бизнес-консультант. Помогайте с идеями, маркетингом, финансами и стратегией.";
            case "Быт" -> "Вы — практичный советчик по бытовым вопросам: кулинария, уборка, организация пространства и др.";
            case "Ремонт" -> "Вы — мастер по ремонту. Давайте пошаговые инструкции по ремонту техники, мебели, электрики и сантехники.";
            case "Советы" -> "Вы — мудрый советчик. Давайте полезные, добрые и практичные рекомендации на любые темы.";
            default -> "Вы — полезный и дружелюбный помощник.";
        };
    }
}