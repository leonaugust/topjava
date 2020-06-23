package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.util.MealsUtil.getSortedListCombinedOf;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    MealService service;

    @Autowired
    MealRepository repository;

    @Test
    public void getAll() {
        List<Meal> all = getSortedListCombinedOf(
                service.getAll(USER_ID).stream(),
                service.getAll(ADMIN_ID).stream());
        assertMatch(all, ADMIN_MEAL, USER_MEAL);
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> all = getSortedListCombinedOf(
                service.getBetweenInclusive(LocalDate.MIN, USER_MEAL_DATE_TIME.toLocalDate(), USER_ID).stream(),
                service.getBetweenInclusive(LocalDate.MIN, USER_MEAL_DATE_TIME.toLocalDate(), ADMIN_ID).stream());
        assertMatch(all, USER_MEAL);
    }

    @Test
    public void get() {
        Meal meal = service.get(USER_MEAL_ID, USER_ID);
        assertMatch(meal, USER_MEAL);
    }

    @Test
    public void getNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.get(ADMIN_MEAL_ID, USER_ID));
    }

    @Test
    public void delete() {
        service.delete(USER_MEAL_ID, USER_ID);
        assertNull(repository.get(USER_MEAL_ID, USER_ID));
    }

    @Test
    public void deleteNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.delete(ADMIN_MEAL_ID, USER_ID));
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(USER_MEAL_ID, USER_ID), updated);
    }

    @Test
    public void updateNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.update(ADMIN_MEAL, USER_ID));
    }

    @Test
    public void create() {
        Meal newMeal = getNew();
        Meal created = service.create(newMeal, USER_ID);
        Integer newId = created.getId();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() throws Exception {
        assertThrows(DataAccessException.class, () ->
                service.create(new Meal(null, USER_MEAL_DATE_TIME, "Duplicate", 0), USER_ID));
    }
}