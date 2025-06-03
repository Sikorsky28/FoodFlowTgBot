package com.example.nutriobot.service;

import com.example.nutriobot.model.UserInput;

public class PromptBuilder {
    public static String build(UserInput input) {
        return String.format(
                "Ты — диетолог. Составь персонализированный рацион питания на 1 день.\n" +
                        "- Возраст: %d лет\n" +
                        "- Вес: %d кг\n" +
                        "- Рост: %d см\n" +
                        "- Пол: %s\n" +
                        "- Уровень активности: %s\n" +
                        "- Локация: %s\n" +
                        "- Бюджет: %.2f рублей в день\n" +
                        "- Цель: %s\n\n" +
                        "⚠️ Требования:\n" +
                        "1. Только рацион питания (завтрак, перекус, обед, ужин).\n" +
                        "2. Без комментариев, рассуждений, вступлений и выводов.\n" +
                        "3. Затем — список покупок по категориям.\n" +
                        "4. Не пиши пояснений. Ответ должен быть минималистичным и структурированным.\n",
                input.getAge(),
                input.getWeight(),
                input.getHeight(),
                input.getGender(),
                input.getActivityLevel(),
                input.getLocation(),
                input.getBudget(),
                input.getGoal()
        );
    }
}
