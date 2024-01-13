package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.model.Film;

import java.util.List;

public interface LikesStorage {

    List<Long> getAllFilmLikes(Long userId);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> getPopularFilms(int count);

    int getLikesCountForFilm(Long filmId);

}
