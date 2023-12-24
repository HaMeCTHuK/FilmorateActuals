package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage extends AbstractStorage<Film> {

    List<Long> getAllFilmLikes(Long userId);

    boolean addLike(Long filmId, Long UserId);

    boolean deleteLike(Long filmId, Long UserId);

    List<Film> getPopularFilms(int count);
}
