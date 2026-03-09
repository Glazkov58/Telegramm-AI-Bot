package com.example.bot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bot.entity.AiRole;

@Repository
public interface AiRoleRepository extends JpaRepository<AiRole, Long> {
    boolean existsByTitle(String name);
    AiRole findByTitle(String title);
    List<AiRole> findByIsActiveTrue();
}
