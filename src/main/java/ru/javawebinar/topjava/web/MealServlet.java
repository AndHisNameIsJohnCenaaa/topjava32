package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.InMemoryMealRepository;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.MealsUtil.START_SEQ;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private final MealRepository mealRepository = new InMemoryMealRepository();
    private static final String LIST_MEALS = "/meals.jsp";
    private static final String INSERT_OR_EDIT = "/mealForm.jsp";
    private final int caloriesPerDay = 2000;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String forward="";
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if (action.equalsIgnoreCase("delete")){
            int mealId = Integer.parseInt(request.getParameter("mealId"));
            mealRepository.delete(mealId);
            forward = LIST_MEALS;
            request.setAttribute("meals",
                    MealsUtil.filteredByStreams(mealRepository.getAllMeals(), LocalTime.MIN, LocalTime.MAX, caloriesPerDay));
        } else if (action.equalsIgnoreCase("edit")){
            forward = INSERT_OR_EDIT;
            int mealId = Integer.parseInt(request.getParameter("mealId"));
            Meal meal = mealRepository.get(mealId);
            request.setAttribute("meal", meal);
        } else if (action.equalsIgnoreCase("list")){
            forward = LIST_MEALS;
            request.setAttribute("meals",
                    MealsUtil.filteredByStreams(mealRepository.getAllMeals(), LocalTime.MIN, LocalTime.MAX, caloriesPerDay));
        } else {
            forward = INSERT_OR_EDIT;
        }

        RequestDispatcher view = request.getRequestDispatcher(forward);
        view.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal();
        meal.setDescription(request.getParameter("description"));
        meal.setCalories(Integer.parseInt(request.getParameter("calories")));
        meal.setDateTime(LocalDateTime.parse(request.getParameter("dateTime"), FORMATTER));
        String mealId = request.getParameter("userid");
        if (mealId == null || mealId.isEmpty()) {
            mealRepository.save(meal);
        }
        else {
            meal.setId(Integer.parseInt(mealId));
            mealRepository.save(meal);
        }
        RequestDispatcher view = request.getRequestDispatcher(LIST_MEALS);
        request.setAttribute("meals",
                MealsUtil.filteredByStreams(mealRepository.getAllMeals(), LocalTime.MIN, LocalTime.MAX, caloriesPerDay));
        view.forward(request, response);
    }

//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        List<MealTo> mealsTo = MealsUtil.filteredByStreams(mealRepository.getAllMeals(), LocalTime.MIN, LocalTime.MAX, caloriesPerDay);
//        request.setAttribute("meals", mealsTo);
//        request.getRequestDispatcher(LIST_MEALS).forward(request, response);
//    }
}
