package ru.java.practicum.filmorate.storage;


import ru.java.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage extends AbstractStorage<Genre>{

    List<Genre> getAll();

    Genre get(Long id);

}
