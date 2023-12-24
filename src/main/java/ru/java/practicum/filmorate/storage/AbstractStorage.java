package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.model.BaseUnit;

import java.util.List;

public interface AbstractStorage <T extends BaseUnit> {

    T create(T data);

    T update(T data);

    List<T> getAll();

    T get(long id);

    void deleteData(T data);
}
