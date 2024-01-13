package ru.java.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.exception.IncorrectParameterException;
import ru.java.practicum.filmorate.exception.ValidationException;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.storage.FilmStorage;
import ru.java.practicum.filmorate.storage.UserStorage;


import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FilmService extends AbstractService<Film> {

    private static final LocalDate LAST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier( "userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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
        if (getData(filmId) == null) {
            throw new DataNotFoundException("Фильма с айди нет" + filmId);
        }
    }

    @Override
    public void validateParameters(Long filmId, Long userId) {
        User user = userStorage.get(userId);
        Film film = getData(filmId);
        if (film == null || user == null) {
            log.info("Ошибка валидации. Проверь null");
            log.info("Ошибка валидации. Такого айди пользователя: {} или фильма {} нет", userId, filmId);
            throw new IncorrectParameterException("Некорректные параметры полей, проверь null");
        }
    }

    public boolean addLike(long filmId, long userId) {
        validateParameters(filmId, userId);
        log.info("Добавляем лайк от пользователя с айди : {} для фильма {}", userId, filmId);
        return filmStorage.addLike(filmId, userId);
    }

    public boolean deleteLike(long filmId, long userId) {
        validateParameters(filmId, userId);
        log.info("Удаляем лайк от пользователя с айди : {}", userId);
        return filmStorage.deleteLike(filmId, userId);
    }

    public List<Long> getAllFilmLikes(Long filmId) {
        validateParameter(filmId);
        log.info("Получаем все лайки фильма от пользователей");
        return filmStorage.getAllFilmLikes(filmId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получаем самые залайканые фильмы количеством: {}", count);
        return filmStorage.getPopularFilms(count);
    }

}
