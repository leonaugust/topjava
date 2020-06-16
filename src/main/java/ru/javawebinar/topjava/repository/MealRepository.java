package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Filter;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.util.Collection;
import java.util.List;

public interface MealRepository {
    // null if not found, when updated
    Meal save(int userId, Meal meal);

    // false if not found
    boolean delete(int userId, int mealId);

    // null if not found
    Meal get(int userId, int mealId);

    List<MealTo> getAllFiltered(int userId, Filter filter, int caloriesPerDay);

    List<Meal> getAll(int userId);
}
