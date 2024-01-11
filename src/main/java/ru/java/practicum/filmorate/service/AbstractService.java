package ru.java.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.BaseUnit;
import ru.java.practicum.filmorate.storage.db.AbstractDbStorage;
import ru.java.practicum.filmorate.storage.memory.InMemoryBaseStorage;

import java.util.*;

@Slf4j
public abstract class AbstractService<T extends BaseUnit> {
    @Autowired
    protected InMemoryBaseStorage<T> inMemoryBaseStorage;
    @Autowired
    protected AbstractDbStorage<T> abstractDbStorage;

    public abstract void validate(T data);

    public abstract void validateParameter(Long id);

    public abstract void validateParameters(Long id, Long otherId);

    public T create(T data) {
        validate(data);
        log.info("добавляем еще пытаемся");
        return abstractDbStorage.create(data);    ///inMemoryBaseStorage
    }

    public T update(T data) {
        validate(data);
        if (abstractDbStorage.get(data.getId()) == null) {    ///inMemoryBaseStorage
            log.info("Данные пользователя не найдены");
            throw new DataNotFoundException("Данные пользователя не найдены");
        }
        return abstractDbStorage.update(data);    ////inMemoryBaseStorage
    }

    public List<T> getAll() {
       return abstractDbStorage.getAll();
    }   ///inMemoryBaseStorage

    public T getData(Long id) {
        if (abstractDbStorage.get(id) == null) {         ///inMemoryBaseStorage
            log.info("Данные пользователя не найдены");
            throw new DataNotFoundException("Данные пользователя не найдены");
        }
        return abstractDbStorage.get(id);     ///inMemoryBaseStorage
    }
}
