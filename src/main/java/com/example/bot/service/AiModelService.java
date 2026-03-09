package com.example.bot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.bot.entity.AiModel;
import com.example.bot.repository.AiModelRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AiModelService {
    
    private final AiModelRepository aiModelRepository;
    
    public AiModelService(AiModelRepository aiModelRepository) {
        this.aiModelRepository = aiModelRepository;
    }

    public List<AiModel> getAll() {
        return aiModelRepository.findAll();
    }

    public String getModelById(Long modelId) {
        return aiModelRepository.findById(modelId)
            .map(AiModel::getKey) // предполагается, что модель хранится в поле `key`
            .orElse("z-ai/glm-4.5-air:free"); // модель по умолчанию
}
}
