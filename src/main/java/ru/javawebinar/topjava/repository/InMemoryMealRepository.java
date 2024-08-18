package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.javawebinar.topjava.util.MealsUtil.START_SEQ;

public class InMemoryMealRepository implements MealRepository {
    AtomicInteger counter = new AtomicInteger(START_SEQ + 6);
    private final Map<Integer,Meal> repository = new ConcurrentHashMap<>();

    {
        List<Meal> meals = Arrays.asList(
                new Meal(START_SEQ, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(START_SEQ + 1, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(START_SEQ + 2, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(START_SEQ + 3, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(START_SEQ + 4, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(START_SEQ + 5, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(START_SEQ + 6, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
        meals.forEach(meal -> repository.put(meal.getId(), meal));
    }

    @Override
    public void save(Meal meal) {
        if (meal.isNew()) {
            repository.put(counter.getAndIncrement(), meal);
        } else {
            repository.put(meal.getId(), meal);
        }
    }

    @Override
    public Meal get(int id) {
        return repository.get(id);
    }

    @Override
    public void delete(int id) {
        repository.remove(id);
    }

    @Override
    public List<Meal> getAllMeals() {
        return new ArrayList<>(repository.values());
    }
}
