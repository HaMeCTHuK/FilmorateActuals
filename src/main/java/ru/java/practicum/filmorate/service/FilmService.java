package ru.java.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.exception.IncorrectParameterException;
import ru.java.practicum.filmorate.exception.ValidationException;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.storage.memory.InMemoryFilmStorage;
import ru.java.practicum.filmorate.storage.memory.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FilmService extends AbstractService<Film> {

    private static final LocalDate LAST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public void validate(Film film) {

        if (film.getReleaseDate().isBefore(LAST_RELEASE_DATE)) {
            log.warn("Дата выпуска меньше 1895.12.28 : {}", film.getReleaseDate());
            throw new ValidationException("Дата выпуска меньше 1895.12.28");
        }
    }

    @Override
    public void validateParameter(Long filmId) {
        if (filmId == null) {
            throw new IncorrectParameterException("Некорректные параметры поля, проверь null");
        }
    }

    @Override
    public void validateParameters(Long filmId, Long userId) {
        User user = inMemoryUserStorage.get(userId);
        Film film = inMemoryFilmStorage.get(filmId);
        if (film == null || user == null) {
            log.info("Ошибка валидации. Проверь null");
            throw new IncorrectParameterException("Некорректные параметры полей, проверь null");
        }

        if (!inMemoryUserStorage.getAll().contains(user) || !inMemoryFilmStorage.getAll().contains(film)) {
            log.info("Ошибка валидации. Пользователей с айди нет" + userId);
            throw new DataNotFoundException("Пользователей с айди нет" + userId);

        }
    }

    public boolean addLike(long filmId, long userId) {
        validateParameters(filmId, userId);
        log.info("Добавляем лайк от пользователя с айди : {} для фильма {}", userId, filmId);
        return inMemoryFilmStorage.addLike(filmId, userId);
    }

    public boolean deleteLike(long filmId, long userId) {
        validateParameters(filmId, userId);
        log.info("Удаляем лайк от пользователя с айди : {}", userId);
        return inMemoryFilmStorage.deleteLike(filmId, userId);
    }

    public List<Long> getAllFilmLikes(Long filmId) {
        validateParameter(filmId);
        log.info("Получаем все лайки фильма от пользователей");
        return inMemoryFilmStorage.getAllFilmLikes(filmId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получаем самые залайканые фильмы количеством: {}", count);
        return inMemoryFilmStorage.getPopularFilms(count);
    }
}
