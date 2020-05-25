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

        System.out.println(filteredByStreamsOptional(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals,
                                                            LocalTime startTime,
                                                            LocalTime endTime,
                                                            int caloriesPerDay) {
        Map<String, Integer> caloriesByDate = new HashMap<>();
        List<UserMeal> filtered = new ArrayList<>();
        for (UserMeal meal : meals) {
            String key = buildKeyFromMealDate(meal);
            int calories = caloriesByDate.getOrDefault(key, 0);
            calories += meal.getCalories();
            caloriesByDate.put(key, calories);

            if (mealIsBetweenTime(meal, startTime, endTime)) {
                filtered.add(meal);
            }
        }

        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : filtered) {
            String key = buildKeyFromMealDate(meal);
            boolean excess = caloriesByDate.get(key) > caloriesPerDay;
            result.add(transformWithExcess(meal, excess));
        }
        return result;
    }


    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals,
                                                             LocalTime startTime,
                                                             LocalTime endTime,
                                                             int caloriesPerDay) {

        Map<String, Integer> caloriesByDate = meals.stream()
                .collect(Collectors.groupingBy(
                        UserMealsUtil::buildKeyFromMealDate,
                        Collectors.summingInt(UserMeal::getCalories)
                ));

        return meals.stream()
                .filter(meal -> mealIsBetweenTime(meal, startTime, endTime))
                .map(meal -> transformWithExcess
                        (meal, caloriesByDate.get(buildKeyFromMealDate(meal)) > caloriesPerDay)
                )
                .collect(Collectors.toList());

        }

    public static List<UserMealWithExcess> filteredByStreamsOptional(List<UserMeal> meals,
                                                                     LocalTime startTime,
                                                                     LocalTime endTime,
                                                                     int caloriesPerDay) {
        return meals
                .parallelStream()
                .collect(new FilterCollector(startTime, endTime, caloriesPerDay));
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

    public static boolean mealIsBetweenTime(UserMeal meal,
                                             LocalTime startTime,
                                             LocalTime endTime) {
        return TimeUtil.isBetweenHalfOpen
                (meal.getDateTime().toLocalTime(), startTime, endTime);
    }

    public static String buildKeyFromMealDate(UserMeal meal) {
        int dayOfMonth = meal.getDateTime().getDayOfMonth();
        int month = meal.getDateTime().getMonthValue();
        int year = meal.getDateTime().getYear();
        return dayOfMonth + "." + month  + "." + year;
    }
}
