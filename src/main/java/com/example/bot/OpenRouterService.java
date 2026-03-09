package com.example.bot;

import reactor.core.publisher.Mono;
import java.util.List;

public interface OpenRouterService {
    Mono<String> getChatResponse(String userMessage);
    Mono<String> getChatResponseWithHistory(List<OpenRouterRequest.Message> history, String model);
    void setAiModel(String model);
}