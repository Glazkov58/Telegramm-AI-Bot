package com.example.bot;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.bot.OpenRouterRequest.Message;

import reactor.core.publisher.Mono;

@Service
public class OpenRouterServiceImpl implements OpenRouterService {

    private final WebClient webClient;
    private final String apiKey;
    private String model;
    private final String apiUrl;
    private final String systemPrompt; // ← добавили

    public OpenRouterServiceImpl(
            WebClient.Builder webClientBuilder,
            @Value("${openrouter.api.key}") String apiKey,
            @Value("${openrouter.api.model}") String model,
            @Value("${openrouter.api.url}") String apiUrl,
            @Value("${openrouter.api.system-prompt}") String systemPrompt // ← внедрили
    ) {
        this.webClient = webClientBuilder.build();
        this.apiKey = apiKey;
        this.model = model;
        this.apiUrl = apiUrl;
        this.systemPrompt = systemPrompt;
    }

    @Override
    public Mono<String> getChatResponse(String userMessage) {
        // Создаём список сообщений: сначала system, потом user
        List<OpenRouterRequest.Message> messages = List.of(
                new OpenRouterRequest.Message("system", systemPrompt),
                new OpenRouterRequest.Message("user", userMessage)
        );

        OpenRouterRequest request = new OpenRouterRequest(model, messages);
        System.out.println("MODEL:" + model);

        return this.webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenRouterResponse.class)
                .map(response -> {
                    if (response != null && response.choices() != null && !response.choices().isEmpty()) {
                        return response.choices().get(0).message().content();
                    }
                    return null;
                })
                .onErrorReturn("");
    }

    @Override
    public Mono<String> getChatResponseWithHistory(List<Message> history, String model) {
        
        throw new UnsupportedOperationException("Unimplemented method 'getChatResponseWithHistory'");
    }

    
    public void setAiModel(String model) {
        this.model = model;        
    }

}