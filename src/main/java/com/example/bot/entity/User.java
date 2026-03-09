package com.example.bot.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "telegram_id")
    private Long telegramId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_blocked")
    private boolean blocked = false;

    @Column(name = "blocked_until")
    private LocalDateTime blockedUntil; // null = навсегда

    @Column(name = "premium_active")
    private boolean premiumActive = false;

    @Column(name = "premium_until")
    private LocalDateTime premiumUntil; // null = не активен

    // Обязательный конструктор без аргументов
    public User() {}

    // Удобный конструктор
    public User(Long id, String username, String firstName, String lastName, String languageCode) {
        this.telegramId = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.languageCode = languageCode;
        this.createdAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры — ОБЯЗАТЕЛЬНЫ
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public LocalDateTime getBlockedUntil() {
        return blockedUntil;
    }

    public void setBlockedUntil(LocalDateTime blockedUntil) {
        this.blockedUntil = blockedUntil;
    }

    public boolean isPremiumActive() {
        return premiumActive;
    }

    public void setPremiumActive(boolean premiumActive) {
        this.premiumActive = premiumActive;
    }

    public LocalDateTime getPremiumUntil() {
        return premiumUntil;
    }

    public void setPremiumUntil(LocalDateTime premiumUntil) {
        this.premiumUntil = premiumUntil;
    }

    public Long getTelegramId() {
        return telegramId;
    }
    public void setTelegramId(Long id){
        telegramId = id;
    }
}
