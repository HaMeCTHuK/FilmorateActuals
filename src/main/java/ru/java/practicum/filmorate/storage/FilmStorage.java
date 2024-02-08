package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage extends AbstractStorage<Film> {

    Mpa getMpaRating(Mpa mpa);

    List<Film> getPopularWithYearForYear(int limit, long genreId, int year);

    List<Film> getPopularWithGenre(int limit, Long genreId);

    List<Film> getPopularWithYear(int limit, Integer year);

}
