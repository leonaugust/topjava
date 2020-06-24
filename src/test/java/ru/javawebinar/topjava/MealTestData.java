package ru.javawebinar.topjava;


import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int NOT_FOUND = 1;
    public static final int USER_ID = START_SEQ;

    public static final int USER_MEAL_ID = START_SEQ + 2;
    public static final int ADMIN_MEAL_ID = START_SEQ + 6;
    public static final LocalDateTime USER_MEAL_DATE_TIME = LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0);
    public static final Meal USER_MEAL = new Meal(USER_MEAL_ID, USER_MEAL_DATE_TIME, "User breakfast", 500);
    public static final Meal ADMIN_MEAL = new Meal(ADMIN_MEAL_ID, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0),
            "Admin dinner", 410);

    public static final List<Meal> USER_MEALS = Arrays.asList(
            new Meal(USER_MEAL_ID + 2, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "User dinner", 500),
            new Meal(USER_MEAL_ID + 1, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "User lunch", 1000),
            USER_MEAL,
            new Meal(USER_MEAL_ID + 3, LocalDateTime.of(2020, Month.JANUARY, 29, 10, 0), "Red lobster", 500));

    public static final List<Meal> FILTERED_USER_MEALS = Arrays.asList(
            USER_MEAL,
            new Meal(USER_MEAL_ID + 3, LocalDateTime.of(2020, Month.JANUARY, 29, 10, 0), "Red lobster", 500));

    public static Meal getNew() {
        return new Meal(null,
                LocalDateTime.of(2020, Month.FEBRUARY, 20, 20, 0),
                "New meal", 1000);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(USER_MEAL);
        updated.setDescription("UpdatedDescription");
        updated.setDateTime(LocalDateTime.MIN);
        updated.setCalories(1999);
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingFieldByFieldElementComparator().isEqualTo(expected);
    }
}

