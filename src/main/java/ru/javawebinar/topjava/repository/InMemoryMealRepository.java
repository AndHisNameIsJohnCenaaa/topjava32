package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.javawebinar.topjava.util.MealsUtil.START_SEQ;

public class InMemoryMealRepository implements MealRepository {
    AtomicInteger counter = new AtomicInteger(START_SEQ + 6);
    Map<Integer,Meal> repository = new ConcurrentHashMap<>();


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
