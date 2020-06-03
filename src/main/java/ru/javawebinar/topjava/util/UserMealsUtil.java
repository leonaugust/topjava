package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.*;

import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

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
                new UserMeal(LocalDateTime.of(2020, Month.FEBRUARY, 8, 10, 0), "С избытком", 2001),
                new UserMeal(LocalDateTime.of(2020, Month.MARCH, 31, 10, 0), "С избытком", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.MARCH, 31, 15, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.MARCH, 31, 17, 0), "Ужин", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.MARCH, 9, 10, 0), "С избытком", 5000),
                new UserMeal(LocalDateTime.of(2021, Month.JANUARY, 10, 10, 0), "С избытком", 1000),
                new UserMeal(LocalDateTime.of(2021, Month.JANUARY, 10, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2021, Month.JANUARY, 10, 20, 0), "Ужин", 1000),
                new UserMeal(LocalDateTime.of(2025, Month.JANUARY, 13, 10, 0), "Без избытка", 100),
                new UserMeal(LocalDateTime.of(2025, Month.JANUARY, 14, 10, 0), "Без избытка", 500),
                new UserMeal(LocalDateTime.of(2019, Month.JUNE, 4, 10, 0), "С избытком", 1000),
                new UserMeal(LocalDateTime.of(2019, Month.JUNE, 4, 14, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2019, Month.JUNE, 4, 20, 0), "Ужин", 1000),
                new UserMeal(LocalDateTime.of(2019, Month.SEPTEMBER, 6, 10, 0), "Без избытка", 500),
                new UserMeal(LocalDateTime.of(2019, Month.SEPTEMBER, 6, 10, 0), "Без избытка(Дубликат)", 500),
                new UserMeal(LocalDateTime.of(2019, Month.SEPTEMBER, 6, 14, 0), "Обед", 600),
                new UserMeal(LocalDateTime.of(2019, Month.SEPTEMBER, 6, 20, 0), "Ужин", 300),
                new UserMeal(LocalDateTime.of(2019, Month.OCTOBER, 6, 11, 0), "Без избытка", 500),
                new UserMeal(LocalDateTime.of(2019, Month.OCTOBER, 6, 11, 0), "Без избытка", 500),
                new UserMeal(LocalDateTime.of(2019, Month.OCTOBER, 6, 14, 0), "Обед", 600),
                new UserMeal(LocalDateTime.of(2019, Month.OCTOBER, 6, 20, 0), "Ужин", 300)
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
        Map<LocalDate, Integer> caloriesByDate = new HashMap<>();
        for (UserMeal meal : meals) {
            LocalDate key = meal.getDateTime().toLocalDate();
            int calories = caloriesByDate.getOrDefault(key, 0);
            calories += meal.getCalories();
            caloriesByDate.put(key, calories);
        }

        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : meals) {
            LocalDate key = meal.getDateTime().toLocalDate();
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
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
        Map<LocalDate, Integer> caloriesByDate = meals.stream()
                .collect(Collectors.groupingBy((meal) -> meal.getDateTime().toLocalDate(),
                        Collectors.summingInt(UserMeal::getCalories))
                );

        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> transformWithExcess(meal, caloriesByDate.get(meal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByStreamsOptional(List<UserMeal> meals,
                                                                     LocalTime startTime,
                                                                     LocalTime endTime,
                                                                     int caloriesPerDay) {
        class FilterCollector implements Collector<UserMeal,
                FilterCollector, List<UserMealWithExcess>> {
            private LocalTime startTime;
            private LocalTime endTime;
            private int caloriesPerDay;
            private int calories;
            private LocalDate previousKey;
            private Set<UserMealWithExcess> filteredMealsTrue;
            private Set<UserMealWithExcess> filteredMealsFalse;
            private Set<UserMealWithExcess> result;
            private  int mealCounter;

            public FilterCollector(LocalTime startTime,
                                   LocalTime endTime,
                                   int caloriesPerDay) {
                this.startTime = startTime;
                this.endTime = endTime;
                this.caloriesPerDay = caloriesPerDay;
                this.filteredMealsTrue = new HashSet<>();
                this.filteredMealsFalse = new HashSet<>();
                this.result = new HashSet<>();
                this.previousKey = meals.get(0).getDateTime().toLocalDate();
                this.mealCounter = 0;
            }

            @Override
            public Supplier<FilterCollector> supplier() {
                return () -> new FilterCollector(startTime, endTime, caloriesPerDay);
            }

            public void accumulate(UserMeal meal) {
                this.calories += meal.getCalories();
                if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                    filteredMealsTrue.add(transformWithExcess(meal, true));
                    filteredMealsFalse.add(transformWithExcess(meal, false));
                }
            }

            @Override
            public BiConsumer<FilterCollector, UserMeal> accumulator() {
                return (supplier, meal) -> {
                    mealCounter++;
                    LocalDate key = meal.getDateTime().toLocalDate();
                    if (!previousKey.equals(key)) {
                        addFilteredToResult(supplier);
                        supplier.calories = 0;
                        supplier.filteredMealsFalse.clear();
                        supplier.filteredMealsTrue.clear();
                        previousKey = key;
                    }

                    supplier.accumulate(meal);

                    if (mealCounter == meals.size()) {
                        addFilteredToResult(supplier);
                    }
                };
            }

            private void addFilteredToResult (FilterCollector supplier) {
                boolean excess = supplier.calories > caloriesPerDay;
                if (excess) {
                    result.addAll(supplier.filteredMealsTrue);
                }
                else {
                    result.addAll(supplier.filteredMealsFalse);
                }
            }

            @Override
            public BinaryOperator<FilterCollector> combiner() {
                //Dangerous:
                //Don't run stream in parallel, you will get NullPointerException.
                return null;
            }

            @Override
            public Function<FilterCollector,
                    List<UserMealWithExcess>> finisher() {
                return supplier -> new ArrayList<>(result);
            }

            @Override
            public Set<Characteristics> characteristics () {
                return Collections.emptySet();
            }
        }

        return meals
                .stream()
                .sorted(Comparator.comparing(UserMeal::getDateTime))
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
}
