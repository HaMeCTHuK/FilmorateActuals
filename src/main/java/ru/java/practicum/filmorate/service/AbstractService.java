package ru.java.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.BaseUnit;
import ru.java.practicum.filmorate.storage.memory.InMemoryBaseStorage;

import java.util.List;

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
        if (!getAll().contains(getData(data.getId()))) {
            log.info("Данные пользователя не найдены");
            throw new DataNotFoundException("Данные пользователя не найдены");
        }
        return inMemoryBaseStorage.update(data);
    }

    public List<T> getAll() {
        return inMemoryBaseStorage.getAll();
    }

    public T getData(Long id) {
        if (!getAll().contains(inMemoryBaseStorage.get(id))) {
            log.info("Данные пользователя не найдены");
            throw new DataNotFoundException("Данные пользователя не найдены");
        }
        return inMemoryBaseStorage.get(id);
    }
}
