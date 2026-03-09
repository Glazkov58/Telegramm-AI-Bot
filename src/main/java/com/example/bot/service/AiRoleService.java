package com.example.bot.service;

import com.example.bot.entity.AiRole;
import com.example.bot.repository.AiRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AiRoleService {

    private final AiRoleRepository aiRoleRepository;

    public AiRoleService(AiRoleRepository aiRoleRepository) {
        this.aiRoleRepository = aiRoleRepository;
    }

    public List<AiRole> findAll() {
        return aiRoleRepository.findAll();
    }

    public List<AiRole> findByIsActiveTrue() {
        return aiRoleRepository.findByIsActiveTrue();
    }

    public AiRole findById(Long id) {
        return aiRoleRepository.findById(id)
            .orElse(null);
    }

    public AiRole save(AiRole role) {
        return aiRoleRepository.save(role);
    }

    public boolean existsByTitle(String title) {
        return aiRoleRepository.existsByTitle(title);
    }

    public void deleteById(Long id) {
        aiRoleRepository.deleteById(id);
    }

    public String getSystemPrompt(String roleName) {
        AiRole role = aiRoleRepository.findByTitle(roleName);
        if (role != null && role.isActive()) {
            return role.getPrompt();
        }
        return "Вы — полезный и дружелюбный помощник.";
    }

    public boolean isValidRole(String roleName) {
        AiRole role = aiRoleRepository.findByTitle(roleName);
        return role != null && role.isActive();
    }

    public AiRole getById(Long id) {
        return aiRoleRepository.findById(id)
            .orElse(null);
    }
}