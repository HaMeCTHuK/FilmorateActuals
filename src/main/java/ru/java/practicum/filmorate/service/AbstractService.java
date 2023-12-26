package ru.java.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.BaseUnit;
import ru.java.practicum.filmorate.storage.memory.InMemoryBaseStorage;

import java.util.HashMap;

@Slf4j
public abstract class AbstractService<T extends BaseUnit> {
    @Autowired
    protected InMemoryBaseStorage<T> inMemoryBaseStorage;

    public abstract void validate(T data);

    public abstract void validateParameter(Long id);

    public abstract void validateParameters(Long id, Long otherId);

    public T create(T data) {
        validate(data);
        log.info("добавляем еще пытаемся");
        return inMemoryBaseStorage.create(data);
    }

    public T update(T data) {
        validate(data);
        if (getAll().get(data.getId()) == null) {
            log.info("Данные пользователя не найдены");
            throw new DataNotFoundException("Данные пользователя не найдены");
        }
        return inMemoryBaseStorage.update(data);
    }

    public HashMap<Long, T> getAll() {
        return inMemoryBaseStorage.getAll();
    }

    public T getData(Long id) {
        if (getAll().get(id) == null) {
            log.info("Данные пользователя не найдены");
            throw new DataNotFoundException("Данные пользователя не найдены");
        }
        return inMemoryBaseStorage.get(id);
    }
}
