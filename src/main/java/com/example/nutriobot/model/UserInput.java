package com.example.nutriobot.model;

public class UserInput {
    private int age;
    private int weight;
    private int height;
    private String gender; // новый параметр
    private String activityLevel;
    private String location;
    private double budget;
    private String goal;

    public UserInput(int age, int weight, String gender,int height,String activityLevel,String location,double budget,String goal ) {
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.activityLevel = activityLevel;
        this.location = location;
        this.budget = budget;
        this.goal = goal;
    }

    public int getAge() { return age; }
    public int getWeight() { return weight; }
    public int getHeight() { return height; }
    public String getGender() { return gender; }
    public String getActivityLevel() { return activityLevel; }
    public String getLocation() { return location; }
    public double getBudget() { return budget; }
    public String getGoal() { return goal; }

    @Override
    public String toString() {
        return String.format(
                "Возраст: %d, Вес: %d, Рост: %d, Пол: %s, Активность: %s, Локация: %s, Бюджет: %.2f, Цель: %s",
                age, weight, height, gender, activityLevel, location, budget, goal
        );
    }
}
