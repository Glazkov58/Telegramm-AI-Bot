package com.example.bot.repository;

import com.example.bot.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Всего пользователей
    long count();

    // Пользователи за последние 24 часа (активные по регистрации)
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= CURRENT_DATE")
    long countToday();

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :sevenDaysAgo")
    long countLast7Days(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);

    @Query("SELECt u FROM User u WHERE telegramId = :id")
    Optional<User> findByTelegramId(@Param("id") Long id);
    
}