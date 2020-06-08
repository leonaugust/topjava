package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.*;
import ru.javawebinar.topjava.model.*;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static String INSERT_OR_EDIT = "meal.jsp";
    private static String LIST_MEAL = "meals.jsp";
    private final MealsDB dbImp = new MealsDBImp();
    private final int NOT_EXISTING_ID = 0;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("doGet");
        String action = request.getParameter("action");
        if (action == null) {
            action = "getAll";
        }
        String forward;

        if (action.equalsIgnoreCase("getAll")) {
            forward = LIST_MEAL;
            requestSetAttributeMealsTo(request);
        } else if (action.equalsIgnoreCase("delete")) {
            forward = LIST_MEAL;
            dbImp.delete(Integer.parseInt(request.getParameter("mealId")));
            requestSetAttributeMealsTo(request);
        } else if (action.equalsIgnoreCase("edit")) {
            forward = INSERT_OR_EDIT;
            int mealId = Integer.parseInt(request.getParameter("mealId"));
            request.setAttribute("mealId", mealId);
            request.setAttribute("meal", dbImp.getById(mealId));
        } else {
            forward = INSERT_OR_EDIT;
            request.setAttribute("meal", new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS),
                    "Описание", 1000, NOT_EXISTING_ID));
        }
        RequestDispatcher view = request.getRequestDispatcher(forward);
        view.forward(request, response);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("doPost");
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt((request.getParameter("calories")));
        int mealId = Integer.parseInt(request.getParameter("mealId"));
        Meal meal = new Meal(dateTime, description, calories, mealId);

        if (mealId == NOT_EXISTING_ID) {
            dbImp.add(meal);
        } else {
            dbImp.update(mealId, meal);
        }

        requestSetAttributeMealsTo(request);
        RequestDispatcher view = request.getRequestDispatcher(LIST_MEAL);
        view.forward(request, response);
    }

    private void requestSetAttributeMealsTo(HttpServletRequest request) {
        List<MealTo> mealsTo =
                MealsUtil.filteredByStreams(dbImp.getAll(), LocalTime.MIN, LocalTime.MAX, 2000);
        request.setAttribute("mealsTo", mealsTo);
    }
}