package ru.java.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.BaseUnit;
import ru.java.practicum.filmorate.storage.AbstractStorage;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractService<T extends BaseUnit> {

    protected AbstractStorage<T> abstractStorage;

    public abstract void validate(T data);

    public abstract void validateParameter(Long id);

    public abstract void validateParameters(Long id, Long otherId);

    public T create(T data) {
        validate(data);
        log.info("Отправляем данные в класс с работой с БД");
        return abstractStorage.create(data);
    }

    public T update(T data) {
        validate(data);
        log.info("Валидация времени прошла успешно");
        if (abstractStorage.get(data.getId()) == null) {
            log.info("Данные пользователя не найдены");
            throw new DataNotFoundException("Данные пользователя не найдены");
        }
        return abstractStorage.update(data);
    }

    public List<T> getAll() {
       return abstractStorage.getAll();
    }

    public T getData(Long id) {
        if (abstractStorage.get(id) == null) {
            log.info("Данные пользователя не найдены");
            throw new DataNotFoundException("Данные пользователя не найдены");
        }
        return abstractStorage.get(id);
    }
}
