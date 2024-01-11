package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage extends AbstractStorage<Genre> {
    @Override
    default List<Genre> getAll() {
        return null;
    }

    @Override
    default Genre get(Long id) {
        return null;
    }
}
