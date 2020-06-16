package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Filter;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private Map<Integer, Map<Integer, Meal>> repository;
    private AtomicInteger counter;

    {
        repository = new ConcurrentHashMap<>();
        counter = new AtomicInteger(0);
        MealsUtil.MEALS.forEach(meal -> save(meal.getUserId(), meal));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            Map<Integer, Meal> userMealsById = repository.getOrDefault(userId, new HashMap<>());
            userMealsById.put(meal.getId(), meal);
            repository.put(userId, userMealsById);
            return meal;
        }
        // handle case: update, but not present in storage
        Map<Integer, Meal> userMealsById = repository.getOrDefault(userId, new HashMap<>());
        Meal result = userMealsById.computeIfPresent(meal.getId(), (mealId, oldMeal) -> meal);
        repository.put(userId, userMealsById);
        return result;
    }

    @Override
    public boolean delete(int userId, int mealId) {
        Map<Integer, Meal> mealsByUserId = repository.get(userId);
        return mealsByUserId.remove(mealId) != null;
    }

    @Override
    public Meal get(int userId, int mealId) {
        Map<Integer, Meal> mealsByUserId = repository.get(userId);
        return mealsByUserId.get(mealId);
    }

    @Override
    public List<MealTo> getAllFiltered(int userId, Filter filter, int caloriesPerDay) {
        return MealsUtil.getFiltered(getAll(userId),
                caloriesPerDay,
                filter);
    }

    @Override
    public List<Meal> getAll(int userId) {
        Collection<Meal> collection = repository.get(userId).values().isEmpty() ?
                Collections.emptyList() :
                repository.get(userId).values();
        List<Meal> meals = new ArrayList<>(collection);
        meals.sort(Comparator.comparing(Meal::getDate).reversed());
        return meals;
    }
}

