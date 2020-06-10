package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MealsDbImp implements MealsDb {
    private Map<Long, Meal> meals = new ConcurrentHashMap<>();
    private AtomicLong idCounter = new AtomicLong(1);

    public MealsDbImp() {
        List<Meal> tempMeals = MealsUtil.getHardcodedMeals();
        tempMeals.forEach(this::add);
    }

    @Override
    public Meal add(Meal meal) {
        meal.setId(idCounter.getAndIncrement());
        return meals.put(meal.getId(), meal);
    }

    @Override
    public Meal getById(Long id) {
        return meals.get(id);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public Meal edit(Meal meal) {
        return meals.computeIfPresent(meal.getId(), (key, value) -> value);
    }

    @Override
    public void delete(Long id) {
        meals.remove(id);
    }
}