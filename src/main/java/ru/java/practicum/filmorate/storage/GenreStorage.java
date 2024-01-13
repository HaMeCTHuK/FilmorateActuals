package ru.java.practicum.filmorate.storage;


import ru.java.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    List<Genre> getAll();

    Genre get(Long id);

}
