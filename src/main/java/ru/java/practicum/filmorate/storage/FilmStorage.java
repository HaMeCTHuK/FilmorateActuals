package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage extends AbstractStorage<Film> {

    Mpa getMpaRating(Mpa mpa);

}
