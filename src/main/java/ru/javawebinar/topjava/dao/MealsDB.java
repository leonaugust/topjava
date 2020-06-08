package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealsDB {
    void add(Meal meal);

    Meal getById(int id);

    List<Meal> getAll();

    void update(int id, Meal meal);

    void delete(int id);
}

