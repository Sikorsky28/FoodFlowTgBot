package com.example.nutriobot.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private Properties properties = new Properties();

    public Config() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Не найден файл application.properties");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return properties.getProperty("bot.username");
    }

    public String getBotToken() {
        return properties.getProperty("bot.token");
    }

    public String getOpenrouterApiKey() {
        return properties.getProperty("openrouter.api.key");
    }
}
