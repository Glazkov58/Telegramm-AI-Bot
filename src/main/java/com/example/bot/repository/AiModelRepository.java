package com.example.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bot.entity.AiModel;

@Repository
public interface AiModelRepository extends JpaRepository<AiModel, Long> {

}
