package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.*;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;
import java.util.stream.Collector;

public class FilterCollector implements Collector<UserMeal,
        Map<Boolean, Set<UserMealWithExcess>>, List<UserMealWithExcess>> {
    private LocalTime startTime;
    private LocalTime endTime;
    private int caloriesPerDay;

    private Map<String, Integer> caloriesByDate;
    private Map<String, Set<UserMealWithExcess>> mealsByDayExcessTrue;
    private Map<String, Set<UserMealWithExcess>> mealsByDayExcessFalse;

    public FilterCollector(LocalTime startTime,
                           LocalTime endTime,
                           int caloriesPerDay) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.caloriesPerDay = caloriesPerDay;
        this.caloriesByDate = new ConcurrentHashMap<>();
        this.mealsByDayExcessTrue = new ConcurrentHashMap<>();
        this.mealsByDayExcessFalse = new ConcurrentHashMap<>();
    }

    @Override
    public Supplier<Map<Boolean, Set<UserMealWithExcess>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<Boolean, Set<UserMealWithExcess>>, UserMeal> accumulator() {
        return (result, meal) -> {
            String key = UserMealsUtil.buildKeyFromMealDate(meal);
            int calories = caloriesByDate.getOrDefault(key, 0);
            caloriesByDate.put(key, calories + meal.getCalories());
            boolean excess = caloriesByDate.get(key) > caloriesPerDay;

            Set<UserMealWithExcess> mealsOfTheDayExcessFalse =
                    mealsByDayExcessFalse.getOrDefault(key, new HashSet<>());
            Set<UserMealWithExcess> mealsOfTheDayExcessTrue =
                    mealsByDayExcessTrue.getOrDefault(key, new HashSet<>());
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime()
                    .toLocalTime(), startTime, endTime)) {
                mealsOfTheDayExcessFalse.add(UserMealsUtil.transformWithExcess(meal, false));
                mealsByDayExcessFalse.put(key, mealsOfTheDayExcessFalse);
                mealsOfTheDayExcessTrue.add(UserMealsUtil.transformWithExcess(meal, true));
                mealsByDayExcessTrue.put(key, mealsOfTheDayExcessTrue);
            }

            Set<UserMealWithExcess> resultMealsFalse = result.getOrDefault(false, new HashSet<>());
            if (excess) {
                Set<UserMealWithExcess> resultMealsTrue = result.getOrDefault(true, new HashSet<>());
                resultMealsFalse.removeAll(mealsOfTheDayExcessFalse);
                result.put(false, resultMealsFalse);
                resultMealsTrue.addAll(mealsOfTheDayExcessTrue);
                result.put(true, resultMealsTrue);
            } else {
                resultMealsFalse.addAll(mealsOfTheDayExcessFalse);
                result.put(false, resultMealsFalse);
            }
        };
    }

    @Override
    public BinaryOperator<Map<Boolean, Set<UserMealWithExcess>>> combiner() {
        return (first, second) -> {

            Set<UserMealWithExcess> secondFalse = second.getOrDefault(false, new HashSet<>());
            Set<UserMealWithExcess> firstTrue = first.getOrDefault(true, new HashSet<>());
            secondFalse.removeAll(firstTrue);

            Set<UserMealWithExcess> firstFalse = first.getOrDefault(false, new HashSet<>());
            Set<UserMealWithExcess> secondTrue = second.getOrDefault(true, new HashSet<>());
            firstFalse.removeAll(secondTrue);

            Set<UserMealWithExcess> combinedTrue = new HashSet<>();
            combinedTrue.addAll(firstTrue);
            combinedTrue.addAll(secondTrue);
            first.put(true, combinedTrue);

            Set<UserMealWithExcess> combinedFalse = new HashSet<>();
            combinedFalse.addAll(firstFalse);
            combinedFalse.addAll(secondFalse);
            first.put(false, combinedFalse);
            return first;
        };
    }

    @Override
    public Function<Map<Boolean,Set<UserMealWithExcess>>,
            List<UserMealWithExcess>> finisher() {
        return result -> {
            List<UserMealWithExcess> combined = new ArrayList<>();
            combined.addAll(result.get(true));
            combined.addAll(result.get(false));
            return combined;
        };
    }

    @Override
    public Set<Characteristics> characteristics () {
        return Collections.emptySet();
    }
}
