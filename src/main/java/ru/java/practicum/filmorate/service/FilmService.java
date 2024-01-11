package ru.java.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.exception.IncorrectParameterException;
import ru.java.practicum.filmorate.exception.ValidationException;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.storage.db.FilmDbStorage;
import ru.java.practicum.filmorate.storage.db.UserDbStorage;
import ru.java.practicum.filmorate.storage.memory.InMemoryFilmStorage;
import ru.java.practicum.filmorate.storage.memory.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FilmService extends AbstractService<Film> {

    private static final LocalDate LAST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
/*    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }*/    //Старая логика

    private final FilmDbStorage filmDbStorage;

    private final UserDbStorage userDbStorage;

    @Autowired
    public FilmService(FilmDbStorage filmDbStorage, UserDbStorage userDbStorage) {
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
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
        User user = userDbStorage.get(userId);   //inMemoryUserStorage
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
        return filmDbStorage.addLike(filmId, userId);    //inMemoryFilmStorage
    }

    public boolean deleteLike(long filmId, long userId) {
        validateParameters(filmId, userId);
        log.info("Удаляем лайк от пользователя с айди : {}", userId);
        return filmDbStorage.deleteLike(filmId, userId);      //inMemoryFilmStorage
    }

    public List<Long> getAllFilmLikes(Long filmId) {
        validateParameter(filmId);
        log.info("Получаем все лайки фильма от пользователей");
        return filmDbStorage.getAllFilmLikes(filmId);   //inMemoryFilmStorage
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получаем самые залайканые фильмы количеством: {}", count);
        return filmDbStorage.getPopularFilms(count);      //inMemoryFilmStorage
    }
}
