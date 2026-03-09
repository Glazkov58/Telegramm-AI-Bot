package com.example.bot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.bot.entity.User;
import com.example.bot.repository.UserRepository;

import io.micrometer.common.lang.NonNull;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User>listUsers() {
        return userRepository.findAll();
    }
    
    public User findById(@NonNull Long id) {
        return userRepository.findById(id).get();
    }
}
