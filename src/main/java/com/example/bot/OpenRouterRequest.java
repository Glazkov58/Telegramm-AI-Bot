package com.example.bot;

import java.util.List;

public record OpenRouterRequest(
    String model,
    List<Message> messages
) {
    public record Message(String role, String content) {}
}