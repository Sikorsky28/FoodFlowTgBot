package com.example.nutriobot.bot;

import com.example.nutriobot.configuration.Config;
import com.example.nutriobot.model.UserInput;
import com.example.nutriobot.service.PromptBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NutrioBot extends TelegramLongPollingBot {

    private final Config config = new Config();

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    public String getAiApiKey() {
        return config.getOpenrouterApiKey();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText().trim();
            System.out.println("🔔 Update получен: " + update);

            switch (text) {
                case "/start":
                    sendMessage(chatId, "Привет! Я Food Flow Bot. Я помогу составить рацион питания. Используй команду /generateplan.");
                    break;
                case "/generateplan":
                    sendMessage(chatId, "❗ Формат команды:\n" +
                            "<возраст> <вес> <рост> <пол> <активность (низкая/средняя/высокая)> <локация> <бюджет> <цель>\n\n" +
                            "Пример:\n30 75 180 мужской средняя Москва 500 похудение");
                    break;
                default:
                    handleGeneratePlan(text, chatId);
                    break;
            }
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleGeneratePlan(String text, long chatId) {
        String[] parts = text.split("\\s+");

        if (parts.length < 8) {
            sendMessage(chatId, "❗ Формат команды:\n<возраст> <вес> <рост> <пол> <активность> <локация> <бюджет> <цель>");
            return;
        }

        try {
            int age = Integer.parseInt(parts[0]);
            int weight = Integer.parseInt(parts[1]);
            int height = Integer.parseInt(parts[2]);
            String gender = parts[3];
            String activity = parts[4];
            String location = parts[5];
            double budget = Double.parseDouble(parts[6]);
            String goal = parts[7];

            // Склеивание локации, если в ней более 1 слова
            if (parts.length > 8) {
                StringBuilder locBuilder = new StringBuilder(parts[5]);
                for (int i = 6; i < parts.length - 2; i++) {
                    locBuilder.append(" ").append(parts[i]);
                }
                location = locBuilder.toString().trim();
                budget = Double.parseDouble(parts[parts.length - 2]);
                goal = parts[parts.length - 1];
            }

            UserInput input = new UserInput(age, weight, gender, height, activity, location, budget, goal);
            String prompt = PromptBuilder.build(input);

            sendMessage(chatId, "🔄 Генерирую рацион питания...");

            String aiResponse = getDeepSeekResponse(prompt);
            sendMessage(chatId, "🤖 AI ответ:\n" + aiResponse);

            System.out.printf("age=%d, weight=%d, height=%d, gender=%s, activity=%s, location=%s, budget=%.2f, goal=%s\n",
                    age, weight, height, gender, activity, location, budget, goal);

        } catch (NumberFormatException e) {
            sendMessage(chatId, "❗ Проверьте числовые значения (возраст, вес, рост, бюджет).");
        } catch (Exception e) {
            sendMessage(chatId, "❗ Ошибка: " + e.getMessage());
        }
    }

    private String getDeepSeekResponse(String userMessage) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String apiKey = config.getOpenrouterApiKey();

            JSONArray messages = new JSONArray();

            // 🔒 Жесткая инструкция, чтобы не тратил токены на рассуждения
            messages.put(new JSONObject()
                    .put("role", "system")
                    .put("content", "Ты — диетолог. Составь рацион питания на 1 день (завтрак, перекус, обед, ужин) и список покупок по категориям. Без формул, без объяснений, без комментариев. Только рацион и список."));

            // 🧠 Запрос пользователя
            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", userMessage));

            JSONObject body = new JSONObject();
            body.put("model", "deepseek/deepseek-r1-0528-qwen3-8b:free");
            body.put("messages", messages);
            body.put("max_tokens", 1000);  // ⬅️ Увеличено с 300 до 1000

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return "⚠️ Ошибка: " + response.statusCode() + "\n" + response.body();
            }

            JSONObject jsonObject = new JSONObject(response.body());
            JSONObject choice = jsonObject.getJSONArray("choices").getJSONObject(0);
            JSONObject messageObj = choice.getJSONObject("message");

            String content = messageObj.optString("content", "").trim();

            if (content.isEmpty()) {
                return "⚠️ Модель не вернула ответ.";
            }

            System.out.println("🧪 RAW RESPONSE:\n" + response.body());

            return content;

        } catch (Exception e) {
            e.printStackTrace();
            return "❗ Ошибка при обращении к DeepSeek.";
        }
    }
}
