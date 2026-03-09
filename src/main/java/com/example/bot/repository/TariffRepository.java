package com.example.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bot.entity.Tariff;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long>{
    
}
