package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.Mpa;

public interface FilmStorage extends AbstractStorage<Film> {

    Mpa getMpaRating(Mpa mpa);

}
