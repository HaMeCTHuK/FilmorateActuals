package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage extends AbstractStorage<Film> {

    List<Long> getAllFilmLikes(Long userId);

    boolean addLike(Long filmId, Long userId);

    boolean deleteLike(Long filmId, Long userId);

    List<Film> getPopularFilms(int count);
}
