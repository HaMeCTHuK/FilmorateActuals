package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.model.BaseUnit;

import java.util.HashMap;

public interface AbstractStorage<T extends BaseUnit> {

    T create(T data);

    T update(T data);

    HashMap<Long, T> getAll();

    T get(long id);

}
