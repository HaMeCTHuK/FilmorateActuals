package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.Mpa;
import ru.java.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class LikesDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private LikesDbStorage likeStorage;
    private FilmDbStorage filmStorage;
    private UserDbStorage userStorage;

    @BeforeEach
    void init() {
        GenreDbStorage genreStorage = new GenreDbStorage(jdbcTemplate);
        DirectorDbStorage directorDbStorage = new DirectorDbStorage(jdbcTemplate);
        likeStorage = new LikesDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, likeStorage, directorDbStorage, genreStorage);
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    void addLike() {

        Film newFilm = new Film(
                "testFilm2",
                "description2",
                LocalDate.of(1999,2,22),
                100,
                0,
                new Mpa(),
                10L);

        newFilm.getMpa().setId(2);

        Film createdFilm = filmStorage.create(newFilm);

        User newUser = new User(
                "user@email.ru",
                "vanya123",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.create(newUser);

        User createdUser = userStorage.get(newUser.getId());

        // Добавляем лайк
        Long filmId = createdFilm.getId();
        Long userId = createdUser.getId();
        likeStorage.addLike(filmId, userId);

        // Проверяем лайк
        long likesCount = likeStorage.getLikesCountForFilm(filmId);
        assertEquals(1, likesCount);
    }

    @Test
    void deleteLike() {

        Film newFilm = new Film(
                "testFilm2",
                "description2",
                LocalDate.of(1999,2,22),
                100,
                0,
                new Mpa(),
                10L);

        newFilm.getMpa().setId(2);
        Film createdFilm = filmStorage.create(newFilm);

        User newUser = new User(
                "user@email.ru",
                "vanya123",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));

        userStorage.create(newUser);
        User createdUser = userStorage.get(newUser.getId());

        // Добавляем лайк
        Long filmId = createdFilm.getId();
        Long userId = createdUser.getId();
        likeStorage.addLike(filmId, userId);

        // Удаляем лайк
        likeStorage.deleteLike(filmId, userId);

        // Проверяем что лайк удален
        long likesCount = likeStorage.getLikesCountForFilm(filmId);
        assertEquals(0, likesCount);
    }

    @Test
    void getLikesCountForFilm() {

        Film newFilm = new Film(
                "testFilm2",
                "description2",
                LocalDate.of(1999,2,22),
                100,
                0,
                new Mpa(),
                10L);

        newFilm.getMpa().setId(2);
        Film createdFilm = filmStorage.create(newFilm);

        User newUser = new User(
                "user@email.ru",
                "vanya123",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));

        userStorage.create(newUser);
        User createdUser = userStorage.get(newUser.getId());

        User newUser2 = new User(
                "user22222@email.ru",
                "Boryan",
                "Drandulet",
                LocalDate.of(2000, 2, 15));

        userStorage.create(newUser2);
        User createdUser2 = userStorage.get(newUser.getId());

        // Добовляем несколько лайков
        Long filmId = createdFilm.getId();
        Long userId1 = createdUser.getId();
        Long userId2 = createdUser2.getId();
        likeStorage.addLike(filmId, userId1);
        likeStorage.addLike(filmId, userId2);

        // Проверяем количество лайков фильма
        long likesCount = likeStorage.getLikesCountForFilm(filmId);
        assertEquals(1, likesCount);
    }

    @Test
    void getAllFilmLikes() {

        Film newFilm = new Film(
                "testFilm1",
                "description1",
                LocalDate.of(1999,2,22),
                100,
                0,
                new Mpa(),
                10L);

        newFilm.getMpa().setId(2);
        Film createdFilm = filmStorage.create(newFilm);

        Film newFilm2 = new Film(
                "testFilm2",
                "description2",
                LocalDate.of(2001,2,22),
                100,
                0,
                new Mpa(),
                10L);

        newFilm2.getMpa().setId(2);
        Film createdFilm2 = filmStorage.create(newFilm2);

        User newUser = new User(
                "user@email.ru",
                "vanya123",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));

        userStorage.create(newUser);
        User createdUser = userStorage.get(newUser.getId());

        // Добавляем лайк разным фильмам
        Long userId = createdUser.getId();
        Long filmId1 = createdFilm.getId();
        Long filmId2 = createdFilm2.getId();
        likeStorage.addLike(filmId1, userId);
        likeStorage.addLike(filmId2, userId);

        // Проверяем фильмы которые лайкнул пользователь
        List<Long> likedFilmIds = likeStorage.getAllFilmLikes(userId);
        assertEquals(2, likedFilmIds.size());
        assertTrue(likedFilmIds.contains(filmId1));
        assertTrue(likedFilmIds.contains(filmId2));
    }

    @Test
    void getPopularFilms() {

        Film newFilm = new Film(
                "testFilm2",
                "description2",
                LocalDate.of(1999,2,22),
                100,
                0,
                new Mpa(),
                10L);

        newFilm.getMpa().setId(2);
        Film createdFilm = filmStorage.create(newFilm);

        Film newFilm2 = new Film(
                "testFilm2",
                "description2",
                LocalDate.of(2001,2,22),
                100,
                0,
                new Mpa(),
                10L);

        newFilm2.getMpa().setId(2);
        Film createdFilm2 = filmStorage.create(newFilm2);

        User newUser = new User(
                "user@email.ru",
                "vanya123",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));

        userStorage.create(newUser);
        User createdUser = userStorage.get(newUser.getId());

        User newUser2 = new User(
                "user22222@email.ru",
                "Boryan",
                "Drandulet",
                LocalDate.of(2000, 2, 15));

        userStorage.create(newUser2);
        User createdUser2 = userStorage.get(newUser.getId());

        // Добавляем лайки разным фильмам
        Long userId1 = createdUser.getId();
        Long userId2 = createdUser2.getId();
        likeStorage.addLike(createdFilm.getId(), userId1);
        likeStorage.addLike(createdFilm.getId(), userId2);
        likeStorage.addLike(createdFilm2.getId(), userId1);

        // Получаем список популярных фильмов
        List<Film> popularFilms = likeStorage.getPopularFilms(2);

        // Проверяем
        assertNotNull(popularFilms);
        assertEquals(2, popularFilms.size());

        // Проверяем популярные фильмы с лайками
        assertTrue(popularFilms.get(0).getLikes() >= popularFilms.get(1).getLikes());
    }
}
