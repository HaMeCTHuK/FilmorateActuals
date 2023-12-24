package ru.java.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage extends InMemoryBaseStorage<Film> implements FilmStorage {

    @Override
    public List<Long> getAllFilmLikes(Long filmId) {
        if (!getAll().contains(get(filmId))) {
            throw new DataNotFoundException("Список не выведен, таких фильмов с айди нет" + filmId);
        }
        Film film = get(filmId);
        return new ArrayList<>(film.getLikes());
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        Film film = get(filmId);
        return film.getLikes().add(userId);
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        Film film = get(filmId);
        boolean isRemoved = film.getLikes().remove(userId);

        if (!isRemoved) {
            throw new DataNotFoundException("Лайк не удален, таких данных в списке лайков нет");
        }

        return isRemoved;
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
