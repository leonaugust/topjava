package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealsDb;
import ru.javawebinar.topjava.dao.MealsDbImp;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final String INSERT_OR_EDIT = "meal.jsp";
    private static final String LIST_MEAL = "meals.jsp";
    private static final String GET_ALL = "meals?action=getAll";
    private static final Long NOT_EXISTING_ID = 0L;
    private static final int DEFAULT_CALORIES_PER_DAY = 2000;
    private MealsDb dbImp;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        dbImp = new MealsDbImp();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action") == null ?
                "getAll" : request.getParameter("action");
        log.debug("method: doGet, action: " + action);

        switch (action) {
            case "insert":
            case "edit":
                Meal meal;
                if (action.equalsIgnoreCase("insert")) {
                    meal = new Meal(NOT_EXISTING_ID,
                            LocalDateTime.now().truncatedTo(ChronoUnit.HOURS),
                            "Описание", 1000);
                } else {
                    meal = dbImp.getById(Long.parseLong(request.getParameter("mealId")));
                }
                log.debug("mealId: " + meal.getId());
                request.setAttribute("meal", meal);
                request.getRequestDispatcher(INSERT_OR_EDIT).forward(request, response);
                break;
            case "delete":
                dbImp.delete(Long.parseLong(request.getParameter("mealId")));
                response.sendRedirect(GET_ALL);
                break;
            default:
                List<MealTo> mealsTo = MealsUtil.filteredByStreams(dbImp.getAll(),
                        LocalTime.MIN, LocalTime.MAX, DEFAULT_CALORIES_PER_DAY);
                request.setAttribute("mealsTo", mealsTo);
                request.getRequestDispatcher(LIST_MEAL).forward(request, response);
                break;
        }
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {
        log.debug("method: doPost");
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt((request.getParameter("calories")));
        Long mealId = Long.parseLong(request.getParameter("mealId"));
        Meal meal = new Meal(mealId, dateTime, description, calories);
        log.debug("mealId: " + mealId);

        if (mealId.equals(NOT_EXISTING_ID)) {
            dbImp.add(meal);
        } else {
            dbImp.edit(meal);
        }

        response.sendRedirect(GET_ALL);
    }
}