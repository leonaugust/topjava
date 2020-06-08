package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealsDBImp implements MealsDB {
    private Map<Integer, Meal> meals = new ConcurrentHashMap<>();
    public static AtomicInteger idCounter = new AtomicInteger(1);

    public MealsDBImp() {
        List<Meal> tempMeals = MealsUtil.getHardcodedMeals();
        tempMeals.forEach(this::add);
    }

    @Override
    public void add(Meal meal) {
        meal.setId(idCounter.get());
        meals.put(meal.getId(), meal);
        idCounter.getAndAdd(1);
    }

    @Override
    public Meal getById(int id) {
        return meals.get(id);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public void update(int id, Meal meal) {
        delete(id);
        meal.setId(id);
        add(meal);
    }

    @Override
    public void delete(int id) {
        meals.remove(id);
    }
}