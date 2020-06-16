package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Filter;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    private static final Logger log = LoggerFactory.getLogger(MealRestController.class);
    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAllFiltered(Filter filter, int caloriesPerDay) {
        int userId = authUserId();
        log.info("getAll");
        return service.getAllFiltered(userId, filter, caloriesPerDay);
    }

    public List<Meal> getAll() {
        int userId = authUserId();
        log.info("getAll");
        return service.getAll(userId);
    }

    public Meal get(int id) throws NotFoundException {
        int userId = authUserId();
        log.info("get {}", id);
        checkOwner(userId, id);
        return service.get(userId, id);
    }

    public Meal create(Meal meal) {
        int userId = authUserId();
        log.info("create {}", meal);
        checkNew(meal);
        return service.create(userId, meal);
    }

    public void delete(int mealId) throws NotFoundException {
        int userId = authUserId();
        log.info("delete {}", mealId);
        checkOwner(userId, mealId);
        service.delete(userId, mealId);
    }

    public void update(Meal meal, int id) throws NotFoundException {
        int userId = authUserId();
        log.info("update {} with id={}", meal, id);
        assureIdConsistent(meal, id);
        checkOwner(userId, id);
        service.update(userId, meal);
    }

    private void checkOwner(int userId, int mealId) {
        Meal meal = service.get(userId, mealId);
        if (userId != meal.getUserId()) {
            throw new NotFoundException("You are not allowed to do this");
        }
    }
}