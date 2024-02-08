package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.Genre;
import ru.java.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private DirectorDbStorage directorDbStorage;
    private LikesDbStorage likeStorage;
    private FilmDbStorage filmStorage;
    private UserDbStorage userStorage;
    private GenreDbStorage genreStorage;

    @BeforeEach
    void init() {
        genreStorage = new GenreDbStorage(jdbcTemplate);
        directorDbStorage = new DirectorDbStorage(jdbcTemplate, genreStorage);
        likeStorage = new LikesDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, likeStorage, directorDbStorage, genreStorage);
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    void testCreateFilm() {
        Film newFilm = new Film(
                "testFilm",
                "description",
                LocalDate.of(1999,2,2),
                150,
                1,
                new Mpa(),
                10L);

        newFilm.getMpa().setId(1);

        Film createdFilm = filmStorage.create(newFilm);

        // Проверяем, что фильм успешно создан
        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm.getName()).isEqualTo(newFilm.getName());

    }

    @Test
    void testUpdateFilm() {
        // Подготавливаем данные для теста
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

        // Меняем данные фильма
        createdFilm.setName("Updated Film");
        createdFilm.setDescription("Updated Description");
        createdFilm.setReleaseDate(LocalDate.now().plusDays(1));
        createdFilm.setDuration(160);
        createdFilm.setRating(4);

        //Устанавливаем MPA
        newFilm.getMpa().setId(4);

        // Обновляем фильм в базе данных
        Film updatedFilm = filmStorage.update(createdFilm);

        // Проверяем, что фильм успешно обновлен
        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getName()).isEqualTo(createdFilm.getName());
    }

    @Test
    void testGetFilm() {
        // Подготавливаем данные для теста
        Film newFilm = new Film(
                "testFilm3",
                "description3",
                LocalDate.of(1999,2,23),
                90,
                1,
                new Mpa(),
                10L);
        newFilm.getMpa().setId(3);

        Film createdFilm = filmStorage.create(newFilm);

        // Получаем фильм по его ID
        Film retrievedFilm = filmStorage.get(createdFilm.getId());

        // Проверяем, что получен правильный фильм
        assertThat(retrievedFilm).isNotNull();
        assertThat(retrievedFilm.getId()).isEqualTo(createdFilm.getId());
        assertThat(retrievedFilm.getName()).isEqualTo(createdFilm.getName());
    }

    @Test
    void testDeleteFilm() {
        // Подготавливаем данные для теста
        Film newFilm = new Film(
                "testFilm4",
                "description4",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                10L);
        newFilm.getMpa().setId(5);

        Film createdFilm = filmStorage.create(newFilm);

        // Удаляем фильм из базы данных
        filmStorage.delete(createdFilm.getId());

        // Пытаемся получить удаленный фильм и ожидаем исключение
        assertThrows(DataNotFoundException.class, () -> filmStorage.get(createdFilm.getId()));
    }

    @Test
    public void isGetPopularWithYearForYearOk() {

        // Подготавливаем данные для теста
        Film newFilm = new Film(
                "testFilm7",
                "description7",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                0L);
        newFilm.getMpa().setId(5);

        Film newFilm2 = new Film(
                "testFilm8",
                "description8",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                0L);
        newFilm2.getMpa().setId(5);

        Film newFilm3 = new Film(
                "testFilm9",
                "description8",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                0L);
        newFilm3.getMpa().setId(5);

        List<Genre> genres = genreStorage.getAll();
        newFilm.setGenres(genres);
        newFilm2.setGenres(genres);

        // Записываем фильмы в базу данных
        Film createdFilm = filmStorage.create(newFilm);
        Film createdFilm2 = filmStorage.create(newFilm2);
        Film createdFilm3 = filmStorage.create(newFilm2);

        List<Film> resultFilm = filmStorage.getPopularWithYearForYear(2,2,1999);

        //Оба фильма должны быть, т.к. год одинаковый и содержат ид жанра 2
        assertEquals(resultFilm.size(),2);
        assertEquals(resultFilm.get(0).getReleaseDate().getYear(), 1999);
        assertTrue(resultFilm.get(0).getGenres().contains(genres.get(2)));

    }

    @Test
    public void isGetPopularWithGenreOk() {

        // Подготавливаем данные для теста
        Film newFilm = new Film(
                "testFilm7",
                "description7",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                0L);
        newFilm.getMpa().setId(5);

        Film newFilm2 = new Film(
                "testFilm8",
                "description8",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                0L);
        newFilm2.getMpa().setId(5);

        Film newFilm3 = new Film(
                "testFilm9",
                "description8",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                0L);
        newFilm3.getMpa().setId(5);

        List<Genre> genres = genreStorage.getAll();
        List<Genre> genres2 = new ArrayList<>();
        genres2.add(genres.get(1));
        newFilm.setGenres(genres);
        newFilm2.setGenres(genres);
        newFilm3.setGenres(genres2);

        // Записываем фильмы в базу данных
        Film createdFilm = filmStorage.create(newFilm);
        Film createdFilm2 = filmStorage.create(newFilm2);
        Film createdFilm3 = filmStorage.create(newFilm3);

        List<Film> resultFilm = filmStorage.getPopularWithGenre(10,4L);

        //2 фильма с айди 4 должны быть в списке
        assertEquals(resultFilm.size(),2);
        assertTrue(resultFilm.get(0).getGenres().contains(genres.get(4)));

    }

    @Test
    public void isGetPopularWithYearOk() {

        // Подготавливаем данные для теста
        Film newFilm = new Film(
                "testFilm7",
                "description7",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                0L);
        newFilm.getMpa().setId(5);

        Film newFilm2 = new Film(
                "testFilm8",
                "description8",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                0L);
        newFilm2.getMpa().setId(5);

        Film newFilm3 = new Film(
                "testFilm9",
                "description8",
                LocalDate.of(2000,2,24),
                40,
                1,
                new Mpa(),
                0L);
        newFilm3.getMpa().setId(5);

        List<Genre> genres = genreStorage.getAll();
        List<Genre> genres2 = new ArrayList<>();
        genres2.add(genres.get(1));
        newFilm.setGenres(genres);
        newFilm2.setGenres(genres);
        newFilm3.setGenres(genres2);

        // Записываем фильмы в базу данных
        Film createdFilm = filmStorage.create(newFilm);
        Film createdFilm2 = filmStorage.create(newFilm2);
        Film createdFilm3 = filmStorage.create(newFilm3);

        List<Film> resultFilm = filmStorage.getPopularWithYear(10,2000);

        //1 фильма с гождом выпуска 2000 должны быть в списке
        assertEquals(resultFilm.size(),1);
        assertEquals(resultFilm.get(0).getReleaseDate().getYear(), 2000);
    }
}
