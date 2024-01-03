package ru.java.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage extends InMemoryBaseStorage<Film> implements FilmStorage {

    @Override
    public List<Long> getAllFilmLikes(Long filmId) {
        Film film = get(filmId);
        return new ArrayList<>(film.getLikes());
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        Film film = get(filmId);
        return  film.addNewLike(userId);
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        Film film = get(filmId);
        return film.deleteLike(userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> allFilms = getAll();
        List<Film> filmsWithMostLikes = allFilms
                .stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());

        return filmsWithMostLikes;
    }
}
