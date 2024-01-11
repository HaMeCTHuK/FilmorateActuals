package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.model.BaseUnit;
import java.util.List;

public interface AbstractStorage<T extends BaseUnit> {

    T create(T data);

    T update(T data);

    List<T> getAll();

    T get(Long id);

    void delete(Long id);

}
