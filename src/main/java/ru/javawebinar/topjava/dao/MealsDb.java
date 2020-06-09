package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealsDb {
    Meal add(Meal meal);

    Meal getById(Long id);

    List<Meal> getAll();

    Meal edit(Meal meal);

    void delete(Long id);
}

