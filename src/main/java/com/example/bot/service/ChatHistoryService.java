package com.example.bot.service;

import com.example.bot.entity.ChatSession;
import com.example.bot.entity.Message;
import com.example.bot.entity.User;
import com.example.bot.repository.ChatSessionRepository;
import com.example.bot.repository.MessageRepository;
import com.example.bot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ChatHistoryService {
    private final UserRepository userRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final MessageRepository messageRepository;

    public ChatHistoryService(UserRepository userRepository,
                              ChatSessionRepository chatSessionRepository,
                              MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.messageRepository = messageRepository;
    }

    public User saveUser(org.telegram.telegrambots.meta.api.objects.User telegramUser) {        
        return userRepository.findByTelegramId(telegramUser.getId())
            .orElseGet(() -> {
                User newUser = new User(
                    (long) telegramUser.getId(),
                    telegramUser.getUserName(),
                    telegramUser.getFirstName(),
                    telegramUser.getLastName(),
                    telegramUser.getLanguageCode()
                );
                return userRepository.save(newUser);
            });
    }

    public ChatSession getOrCreateSession(Long userId, String defaultModel) {
        return chatSessionRepository.findByUserId(userId)
            .orElseGet(() -> {
                ChatSession session = new ChatSession(userId, defaultModel);
                return chatSessionRepository.save(session);
            });
    }

    public void saveMessage(Long sessionId, String role, String content) {
        Message message = new Message(sessionId, role, content);
        messageRepository.save(message);
    }

    public List<Message> getChatHistory(Long sessionId) {
        return messageRepository.findBySessionIdOrderByTimestamp(sessionId);
    }

    public void updateModel(Long userId, String newRoleId) {
        ChatSession session = chatSessionRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Session not found for user: " + userId));
        var roleId = Integer.parseInt(newRoleId);
        session.setRoleId(roleId);
        chatSessionRepository.save(session);
    }
    
    public void updateModelForUser(long userId, String model) {
        System.out.println("User id: " + userId);
        System.out.println("Model: " + model);
        var session = chatSessionRepository.findByUserId(userId).get();
        session.setModelName(model);
        chatSessionRepository.save(session);

    }
}
