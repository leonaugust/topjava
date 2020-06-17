package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private Map<Integer, Map<Integer, Meal>> repository;
    private AtomicInteger counter;

    {
        repository = new ConcurrentHashMap<>();
        counter = new AtomicInteger(0);
        List<Meal> meals = MealsUtil.MEALS;
        save(1, meals.get(0));
        save(2, meals.get(1));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        Map<Integer, Meal> userMealsById = repository.computeIfAbsent(userId, meals -> new ConcurrentHashMap<>());
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            userMealsById.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        return userMealsById.computeIfPresent(meal.getId(), (mealId, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int userId, int mealId) {
        return userExists(userId) && repository.get(userId).remove(mealId) != null;
    }

    @Override
    public Meal get(int userId, int mealId) {
        if (userExists(userId)) {
            return repository.get(userId).get(mealId);
        } else {
            throw new NotFoundException("User not exists");
        }
    }

    @Override
    public List<Meal> getFilteredByDates(int userId, LocalDate startDate, LocalDate endDate) {
        if (!userExists(userId)) {
            throw new NotFoundException("User not exists");
        }

        return repository.get(userId).values().stream()
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .filter(meal -> DateTimeUtil.isBetweenOpen(meal.getDateTime().toLocalDate(),
                        startDate, endDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAll(int userId) {
        Map<Integer, Meal> userMealsById = repository.get(userId);
        if (!userExists(userId)) {
            throw new NotFoundException("User not exists");
        }
        if (userMealsById.isEmpty()) {
            return Collections.emptyList();
        }

        return userMealsById.values().stream()
                .sorted(Comparator.comparing(Meal::getDate).reversed())
                .collect(Collectors.toList());
    }

    private boolean userExists(int userId) {
        return repository.get(userId) != null;
    }
}

