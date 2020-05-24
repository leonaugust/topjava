package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals,
                                                            LocalTime startTime,
                                                            LocalTime endTime,
                                                            int caloriesPerDay) {
        Map<Integer, List<UserMeal>> groups = new HashMap<>();
        List<UserMeal> filtered = new ArrayList<>();
        for (UserMeal meal : meals) {
            sortByDay(groups, meal);
            filterMeal(filtered, meal, startTime, endTime);
        }
        return transformMeals(groups, filtered, caloriesPerDay);
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals,
                                                             LocalTime startTime,
                                                             LocalTime endTime,
                                                             int caloriesPerDay) {
        Map<Integer, List<UserMeal>> groups = new HashMap<>();
        List<UserMeal> filtered = meals.stream()
                .peek((meal) -> sortByDay(groups, meal))
                .filter(meal -> mealIsBetweenTime(meal, startTime, endTime))
                .collect(Collectors.toList());

        return filtered.stream()
                .map((meal) -> transformWithExcess
                        (meal, sumCalories(groups.get(meal.getDay())) > caloriesPerDay
                        )
                )
                .collect(Collectors.toList());
        }

    public static List<UserMealWithExcess> filteredByStreamsOptional(List<UserMeal> meals,
                                                                     LocalTime startTime,
                                                                     LocalTime endTime,
                                                                     int caloriesPerDay) {
        return meals.stream()
                .collect(FilterCollector
                        .filterMeals(startTime, endTime, caloriesPerDay));
    }

    private static void sortByDay(Map<Integer, List<UserMeal>> groups,
                                  UserMeal meal) {
        int day = meal.getDay();
        List<UserMeal> dayMeals = groups.getOrDefault(day, new ArrayList<>());
        dayMeals.add(meal);
        groups.put(day, dayMeals);
    }

    private static List<UserMealWithExcess> transformMeals(Map<Integer, List<UserMeal>> groups,
                                                           List<UserMeal> filtered,
                                                           int caloriesPerDay) {
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : filtered) {
            boolean excess = evaluateExcess(groups, meal, caloriesPerDay);
            result.add(transformWithExcess(meal, excess));
        }
        return result;
    }

    public static UserMealWithExcess transformWithExcess(UserMeal meal,
                                                          boolean excess) {
        return new UserMealWithExcess(
                meal.getDateTime(),
                meal.getDescription(),
                meal.getCalories(),
                excess
        );
    }

    private static boolean evaluateExcess(Map<Integer, List<UserMeal>> groups,
                                          UserMeal meal,
                                          int caloriesPerDay) {
        int day = meal.getDay();
        List<UserMeal> dayMeals = groups.get(day);
        int calories = sumCalories(dayMeals);
        return calories > caloriesPerDay;
    }

    private static int sumCalories(List<UserMeal> group) {
        int calories = 0;
        for (UserMeal meal : group) {
            calories += meal.getCalories();
        }
        return calories;
    }

    public static void filterMeal(List<UserMeal> filtered,
                                   UserMeal meal,
                                   LocalTime startTime,
                                   LocalTime endTime) {
        if (mealIsBetweenTime(meal, startTime, endTime)) {
            filtered.add(meal);
        }
    }

    private static boolean mealIsBetweenTime(UserMeal meal,
                                             LocalTime startTime,
                                             LocalTime endTime) {
        return TimeUtil.isBetweenHalfOpen
                (meal.getDateTime().toLocalTime(), startTime, endTime);
    }
}
