package ru.javawebinar.topjava.service.datajpa;


import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.AbstractMealServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.NOT_FOUND;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaMealServiceTest extends AbstractMealServiceTest {

    @Test
    public void getMealWithUser() {
        Meal actual = service.getMealWithUser(ADMIN_MEAL_ID, ADMIN_ID);
        MEAL_MATCHER.assertMatch(actual, ADMIN_MEAL1);
        USER_MATCHER.assertMatch(actual.getUser(), ADMIN);
    }

    @Test
    public void getMealWithUserNotOwn() {
        assertThrows(NotFoundException.class, () -> service.getMealWithUser(ADMIN_MEAL_ID, USER_ID));
    }

    @Test
    public void getMealWithUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.getMealWithUser(NOT_FOUND, USER_ID));
    }
}
