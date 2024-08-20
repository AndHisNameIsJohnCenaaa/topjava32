package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {   repository.put(0, new HashMap<>());
        repository.put(1, new HashMap<>());
        MealsUtil.meals.forEach(meal -> this.repository.get(0).put(counter.incrementAndGet(), meal));
        MealsUtil.meals.forEach(meal -> this.repository.get(1).put(counter.incrementAndGet(), meal));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        repository.putIfAbsent(userId, new HashMap<>());
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.get(userId).computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
            return meal;
        }
        // handle case: update, but not present in storage
        return repository.get(userId).computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int userId, int id) {
        return repository.getOrDefault(userId, Collections.emptyMap()).remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        return repository.getOrDefault(userId, Collections.emptyMap()).get(id);
    }

    @Override
    public Collection<Meal> getAll(int userId, LocalDate from, LocalDate to) {
        return repository.getOrDefault(userId, Collections.emptyMap()).values().stream()
                .filter(meal -> DateTimeUtil.isBetweenHalfOpen(meal.getDate(), from, to))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed()).collect(Collectors.toList());
    }
}

