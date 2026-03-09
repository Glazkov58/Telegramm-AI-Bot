package com.example.bot;

import com.example.bot.entity.AiModel;
import com.example.bot.entity.AiRole;
import com.example.bot.entity.ChatSession;
import com.example.bot.entity.Message;
import com.example.bot.entity.User;
import com.example.bot.service.AiModelService;
import com.example.bot.service.AiRoleService;
import com.example.bot.service.ChatHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.example.bot.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final OpenRouterService openRouterService;
    private final ChatHistoryService chatHistoryService;
    private final UserRepository userRepository;
    private final AiRoleService aiRoleService;
    private final AiModelService aiModelService;

    private static final int MAX_MESSAGE_LENGTH = 4000;

    public MyTelegramBot(OpenRouterService openRouterService, ChatHistoryService chatHistoryService, UserRepository userRepository, AiRoleService aiRoleService, AiModelService aiModelService) {
        this.openRouterService = openRouterService;
        this.chatHistoryService = chatHistoryService;
        this.userRepository = userRepository;
        this.aiRoleService = aiRoleService;
        this.aiModelService = aiModelService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===

    private boolean handleBlockedUser(User user, long chatId) {
        if (user.isBlocked()) {
            if (user.getBlockedUntil() != null && user.getBlockedUntil().isBefore(LocalDateTime.now())) {
                user.setBlocked(false);
                user.setBlockedUntil(null);
                userRepository.save(user);
            } else {
                sendLongMessage(chatId, "Вы заблокированы.");
                return true;
            }
        }
        return false;
    }
    
    private InlineKeyboardButton createRoleButton(String roleName, Long roleId) {
    InlineKeyboardButton button = new InlineKeyboardButton();
    button.setText(roleName);
    button.setCallbackData("role:" + roleId); // например: "role:Программирование"
    return button;
}

    private void sendLongMessage(long chatId, String text) {
        if (text == null || text.isEmpty()) return;
        if (text.length() <= MAX_MESSAGE_LENGTH) {
            executeSendMessage(chatId, text);
            return;
        }
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + MAX_MESSAGE_LENGTH, text.length());
            if (end < text.length()) {
                int lastSpace = text.lastIndexOf(' ', end - 1);
                if (lastSpace > start) {
                    end = lastSpace;
                }
            }
            String part = text.substring(start, end);
            executeSendMessage(chatId, part);
            start = end;
            while (start < text.length() && text.charAt(start) == ' ') {
                start++;
            }
        }
    }

    private void executeSendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup createRoleSelectionKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<AiRole> activeRoles = aiRoleService.findByIsActiveTrue();
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        for (AiRole role : activeRoles) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(role.getTitle());
            // Формат: role:{id}
            button.setCallbackData("role:" + role.getId());
            System.out.println("Кнопка - " + "role:" + role.getId());
            currentRow.add(button);

            // 2 кнопки в строке
            if (currentRow.size() == 2) {
                rows.add(currentRow);
                currentRow = new ArrayList<>();
            }
        }

        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Проверяем обновления...");

        // === Обработка нажатия на inline-кнопки выбора роли ===
        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            long userId = update.getCallbackQuery().getFrom().getId();

            if (data.startsWith("role:")) {
                String roleId = data.substring(5); // "role:Программирование" → "Программирование"
                Long roleIdLong = Long.parseLong(roleId);
                String roleName = aiRoleService.getById(roleIdLong).getTitle();
                System.out.println("Получена роль: " + roleId);
                // Сохраняем роль как модель (временно — можно улучшить)
                chatHistoryService.updateModel(userId, roleId);

                SendMessage msg = new SendMessage();
                msg.setChatId(String.valueOf(chatId));
                msg.setText("Роль «" + roleName + "» выбрана. Задавайте вопрос!");
                try {
                    execute(msg);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        // === Обработка текстовых сообщений ===
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            org.telegram.telegrambots.meta.api.objects.User telegramUser = update.getMessage().getFrom();

            // Сохраняем пользователя
            User user = chatHistoryService.saveUser(telegramUser);

            if (messageText.equals("/start")) {
                // Отправляем выбор роли
                SendMessage msg = new SendMessage();
                msg.setChatId(String.valueOf(chatId));
                msg.setText("Привет! Выберите роль для общения с ИИ:");

                // Формируем inline-кнопки
                //InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                InlineKeyboardMarkup keyboard = createRoleSelectionKeyboard();
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();

                // Получаем активные роли из БД
                List<AiRole> activeRoles = aiRoleService.findByIsActiveTrue();

                
                // Группируем по 2 кнопки в строке
                for (int i = 0; i < activeRoles.size(); i += 1) {
                    List<InlineKeyboardButton> row = new ArrayList<>();
                    row.add(createRoleButton(activeRoles.get(i).getTitle(), activeRoles.get(i).getId()));
                    //if (i + 1 < activeRoles.size()) {
                        //row.add(createRoleButton(activeRoles.get(i).getTitle(), activeRoles.get(i).getId()));
                    //}
                    rows.add(row);
                }
                


                keyboard.setKeyboard(rows);
                msg.setReplyMarkup(keyboard);

                try {
                    execute(msg);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                return;
            }

            if (messageText.equals("/help")) {
                sendLongMessage(chatId, "Справка:\n• Нажмите /start, чтобы выбрать роль\n• После выбора — задавайте вопросы");
                return;
            }

            // Получаем сессию
            ChatSession session = chatHistoryService.getOrCreateSession(user.getTelegramId(), "z-ai/glm-4.5-air:free");

            // Сохраняем сообщение пользователя
            chatHistoryService.saveMessage(session.getId(), "user", messageText);

            // Разбираем модель и промт
            /*
            String[] parts = session.getModelName().split("\\|\\|", 2);
            String systemPrompt = parts.length > 0 ? parts[0] : "Вы — полезный помощник.";
            String model = parts.length > 1 ? parts[1] : "z-ai/glm-4.5-air:free";
            */
            var roleId = session.getRoleId();
            AiRole selectedRole = aiRoleService.getById(Integer.toUnsignedLong(roleId));
            String model = aiModelService.getModelById(selectedRole.getModel().getId());
            String systemPrompt = selectedRole.getPrompt();


            // Формируем историю
            List<OpenRouterRequest.Message> history = new ArrayList<>();
            history.add(new OpenRouterRequest.Message("system", systemPrompt));

            List<Message> dbMessages = chatHistoryService.getChatHistory(session.getId());
            for (Message msg : dbMessages) {
                String role = "user".equals(msg.getRole()) ? "user" : "assistant";
                history.add(new OpenRouterRequest.Message(role, msg.getContent()));
            }

            // Отправляем запрос
            //String aiResponse = openRouterService.getChatResponseWithHistory(history, model).block();
            /*
            String aiResponse = "";
            for (int i = 0; i < 3; i++) {
                aiResponse = openRouterService.getChatResponse(history.toString()).block();
                if (aiResponse == null) {
                    aiResponse = "Извините, не удалось получить ответ от ИИ.";
                    try {
                        Thread.sleep(i * 1000L);
                    } catch (Exception ex) { }
                    continue;
                }
                break;
            }
            */

            // Отправляем запрос с поддержкой резервной модели
                String aiResponse = null;
                String currentModel = model;
                String fallbackModel = "z-ai/glm-4.5-air:free"; // модель по умолчанию

                // Попытки с основной моделью
                for (int attempt = 0; attempt < 3; attempt++) {
                    aiResponse = openRouterService.getChatResponse(history.toString()).block();
                    if (aiResponse != null && aiResponse != "") {
                        break;
                    }
                    try {
                        Thread.sleep(attempt * 1000L);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                // Если основная модель не ответила — пробуем запасную
                if (aiResponse == null || aiResponse == "") {
                    currentModel = fallbackModel;
                    for (int attempt = 0; attempt < 3; attempt++) {
                        //aiResponse = openRouterService.getChatResponseWithHistory(history, currentModel).block();
                        aiResponse = openRouterService.getChatResponse(history.toString()).block();
                        if (aiResponse != null) {
                            // Опционально: уведомить пользователя, что используется запасная модель
                            // sendLongMessage(chatId, "[Используется запасная модель]");
                            break;
                        }
                        try {
                            Thread.sleep(attempt * 1000L);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }

                if (aiResponse == null || aiResponse == "") {
                    aiResponse = "Извините, не удалось получить ответ от ИИ.";
                } 

            // Сохраняем ответ
            chatHistoryService.saveMessage(session.getId(), "assistant", aiResponse);
            aiResponse = markdownToPlainText(aiResponse);
            sendLongMessage(chatId, aiResponse);
        }        
    }

    public String markdownToPlainText(String markdownText) {
        if (markdownText == null) {
            return null;
        }

        // Удаляем экранированные символы (например, \*)
        String plainText = markdownText.replaceAll("\\\\([\\*_\\[\\]\\(\\)\\~\\`\\>\\#\\+\\-\\=\\|\\{\\}\\.!])", "$1");

        // Убираем разметку жирного, курсива, зачёркнутого и моноширинного текста
        // Обратите внимание: это упрощённый подход, не учитывающий вложенные или непарные теги
        plainText = plainText.replaceAll("\\*{1,2}([^*]+)\\*{1,2}", "$1"); // * или **
        plainText = plainText.replaceAll("_([^_]+)_", "$1");               // курсив
        plainText = plainText.replaceAll("~([^~]+)~", "$1");               // зачёркнутый
        plainText = plainText.replaceAll("```[^`]*```", "");               // многострочный код (удаляем полностью)
        plainText = plainText.replaceAll("`([^`]+)`", "$1");               // inline код

        // Убираем ссылки вида [text](url) → оставляем только text
        plainText = plainText.replaceAll("\\[([^\\[]+)\\]\\([^)]+\\)", "$1");

        // Убираем неразрывные пробелы и другие спецсимволы, если нужно
        // (опционально)

        return plainText;
    }
}