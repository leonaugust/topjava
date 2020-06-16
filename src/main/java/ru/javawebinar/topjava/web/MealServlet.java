package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Filter;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.inmemory.InMemoryMealRepository;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    private MealRestController controller;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        controller = new MealRestController(new MealService(new InMemoryMealRepository()));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if (action == null) {
            String id = request.getParameter("id");
            Meal meal = new Meal(SecurityUtil.authUserId(),
                    id.isEmpty() ? null : Integer.valueOf(id),
                    LocalDateTime.parse(request.getParameter("dateTime")),
                    request.getParameter("description"),
                    Integer.parseInt(request.getParameter("calories")));
            log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
            if (meal.isNew()) {
                controller.create(meal);
            } else {
                controller.update(meal, meal.getId());
            }
            response.sendRedirect("meals");
        } else {
            LocalDate startDate = request.getParameter("startDate").isEmpty() ?
                    LocalDate.MIN :
                    LocalDate.parse(request.getParameter("startDate"));
            LocalTime startTime = request.getParameter("startTime").isEmpty() ?
                    LocalTime.MIN :
                    LocalTime.parse(request.getParameter("startTime"));
            LocalDate endDate = request.getParameter("endDate").isEmpty() ?
                    LocalDate.MAX :
                    LocalDate.parse(request.getParameter("endDate"));
            LocalTime endTime = request.getParameter("endTime").isEmpty() ?
                    LocalTime.MAX :
                    LocalTime.parse(request.getParameter("endTime"));
            log.info("applyFilter(startDate{}, startTime{}, endDate{}, endTime{})",
                    startDate, startTime, endDate, endTime);

            List<MealTo> mealsTo = controller.getAllFiltered(new Filter(startDate, startTime,
                    endDate, endTime), MealsUtil.DEFAULT_CALORIES_PER_DAY);
            request.setAttribute("meals", mealsTo);
            request.getRequestDispatcher("/meals.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                controller.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(SecurityUtil.authUserId(),
                                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        controller.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                request.setAttribute("meals", MealsUtil.getTos(controller.getAll(),
                        MealsUtil.DEFAULT_CALORIES_PER_DAY));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
