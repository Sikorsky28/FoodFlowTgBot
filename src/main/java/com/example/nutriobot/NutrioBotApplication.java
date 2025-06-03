package com.example.nutriobot;

import com.example.nutriobot.bot.NutrioBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class NutrioBotApplication {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new NutrioBot());
            System.out.println("🤖 Бот запущен!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
