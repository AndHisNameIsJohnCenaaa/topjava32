package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealRepository {

    void save(Meal meal);
    Meal get(int id);
    void delete(int id);
    List<Meal> getAllMeals();

}

