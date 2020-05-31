package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.*;

import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Без избытка", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "С избытком", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410),

                new UserMeal(LocalDateTime.of(2020, Month.FEBRUARY, 8, 10, 0), "С избытком", 2001),
                new UserMeal(LocalDateTime.of(2020, Month.MARCH, 31, 10, 0), "С избытком", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.MARCH, 31, 15, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.MARCH, 31, 17, 0), "Ужин", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.MARCH, 9, 10, 0), "С избытком", 5000),
                new UserMeal(LocalDateTime.of(2021, Month.JANUARY, 10, 10, 0), "С избытком", 1000),
                new UserMeal(LocalDateTime.of(2021, Month.JANUARY, 10, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2021, Month.JANUARY, 10, 20, 0), "Ужин", 1000),
                new UserMeal(LocalDateTime.of(2025, Month.JANUARY, 13, 10, 0), "Без избытка", 100),
                new UserMeal(LocalDateTime.of(2025, Month.JANUARY, 14, 10, 0), "Без избытка", 500)

        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println("_______");
        List<UserMealWithExcess> mealsByStreams = filteredByStreamsOptional(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsByStreams.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals,
                                                            LocalTime startTime,
                                                            LocalTime endTime,
                                                            int caloriesPerDay) {
        Map<String, Integer> caloriesByDate = new HashMap<>();
        for (UserMeal meal : meals) {
            String key = buildKeyFromMealDate(meal);
            int calories = caloriesByDate.getOrDefault(key, 0);
            calories += meal.getCalories();
            caloriesByDate.put(key, calories);
        }

        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : meals) {
            String key = buildKeyFromMealDate(meal);
            if (TimeUtil.isBetweenHalfOpen
                    (meal.getDateTime().toLocalTime(), startTime, endTime)) {
                boolean excess = caloriesByDate.get(key) > caloriesPerDay;
                result.add(transformWithExcess(meal, excess));
            }
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
                .filter(meal ->
                        TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
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
                .stream()
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

    public static String buildKeyFromMealDate(UserMeal meal) {
        return meal.getDateTime()
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }
}
