package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FilterCollector implements Collector<UserMeal,
        HashMap<String, Integer>, List<UserMealWithExcess>> {

    private LocalTime startTime;
    private LocalTime endTime;
    private int caloriesPerDay;
    private List<UserMeal> filtered = new CopyOnWriteArrayList<>();

    public FilterCollector(LocalTime startTime,
                           LocalTime endTime,
                           int caloriesPerDay) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.caloriesPerDay = caloriesPerDay;
    }

    @Override
    public Supplier<HashMap<String, Integer>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<HashMap<String, Integer>, UserMeal> accumulator() {
        return (groups, meal) -> {
            String key = UserMealsUtil.buildKeyFromMealDate(meal);
            int calories = groups.getOrDefault(key, 0);
            groups.put(key, calories + meal.getCalories());
            if (UserMealsUtil.mealIsBetweenTime(meal, startTime, endTime)) {
                filtered.add(meal);
            }
        };
    }

    @Override
    public BinaryOperator<HashMap<String, Integer>> combiner() {
        return (first, second) -> {
            second.forEach(
                    (key, value) -> first.merge(key, value, Integer::sum)
            );
            return first;
        };
    }

    @Override
    public Function<HashMap<String, Integer>,
            List<UserMealWithExcess>> finisher() {
        return caloriesByDate -> filtered.stream()
                .map((meal) -> UserMealsUtil.transformWithExcess
                        (meal, caloriesByDate.get(UserMealsUtil.buildKeyFromMealDate(meal)) > caloriesPerDay)
                )
                .collect(Collectors.toList());
    }

    @Override
    public Set<Characteristics> characteristics () {
        return Collections.emptySet();
    }
}
