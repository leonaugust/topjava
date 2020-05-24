package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalTime;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FilterCollector implements Collector<UserMeal,
        HashMap<Integer, Integer>, List<UserMealWithExcess>> {

    private LocalTime startTime;
    private LocalTime endTime;
    private int caloriesPerDay;

    private List<UserMeal> filtered = new ArrayList<>();

    public FilterCollector(LocalTime startTime,
                           LocalTime endTime,
                           int caloriesPerDay) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.caloriesPerDay = caloriesPerDay;
    }

    public static FilterCollector filterMeals(LocalTime startTime,
                                              LocalTime endTime,
                                              int caloriesPerDay) {
        return new FilterCollector(startTime, endTime, caloriesPerDay);
    }

    @Override
    public Supplier<HashMap<Integer, Integer>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<HashMap<Integer, Integer>, UserMeal> accumulator() {
        return (groups, meal) -> {
            int day = meal.getDay();
            int calories = groups.getOrDefault(day, 0);
            groups.put(day, calories + meal.getCalories());
            UserMealsUtil.filterMeal(filtered, meal, startTime, endTime);
        };
    }

    @Override
    public BinaryOperator<HashMap<Integer, Integer>> combiner() {
        return (first, second) -> {
            first.putAll(second);
            return first;
        };
    }

    @Override
    public Function<HashMap<Integer, Integer>,
            List<UserMealWithExcess>> finisher() {
        return groups -> filtered.stream()
                .map((meal) -> UserMealsUtil.transformWithExcess
                        (meal, (groups.get(meal.getDay())) > caloriesPerDay)
                )
                .collect(Collectors.toList());
    }

    @Override
    public Set<Characteristics> characteristics () {
        return Collections.emptySet();
    }
}
