package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.Mpa;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Test
    void testCreateFilm() {
        // Подготавливаем данные для теста


        Film newFilm = new Film(
                "testFilm",
                "description",
                LocalDate.of(1999,2,2),
                150,
                1,
                10L);

        // Записываем фильм в базу данных
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
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
                10L);




        // Записываем фильм в базу данных
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        Film createdFilm = filmStorage.create(newFilm);

        // Меняем данные фильма
        createdFilm.setName("Updated Film");
        createdFilm.setDescription("Updated Description");
        createdFilm.setReleaseDate(LocalDate.now().plusDays(1));
        createdFilm.setDuration(160);
        createdFilm.setRating(4);

        //Устанавливаем MPA
        newFilm.getMpa().setId(1);
        newFilm.getMpa().setName("PG");

        // Обновляем фильм в базе данных
        Film updatedFilm = filmStorage.update(createdFilm);

        // Проверяем, что фильм успешно обновлен
        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getName()).isEqualTo(createdFilm.getName());

    }

    @Test
    void testGetFilm() {
        // Подготавливаем данные для теста
        Film newFilm = new Film();
        newFilm.setName("Test Film");
        newFilm.setDescription("Test Description");
        newFilm.setReleaseDate(LocalDate.now());
        newFilm.setDuration(120);
        newFilm.setRating(5);

        // Записываем фильм в базу данных
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
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
        Film newFilm = new Film();
        newFilm.setName("Test Film");
        newFilm.setDescription("Test Description");
        newFilm.setReleaseDate(LocalDate.now());
        newFilm.setDuration(120);
        newFilm.setRating(5);

        // Записываем фильм в базу данных
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        Film createdFilm = filmStorage.create(newFilm);

        // Удаляем фильм из базы данных
        filmStorage.delete(createdFilm.getId());

        // Пытаемся получить удаленный фильм и ожидаем исключение
        assertThrows(DataNotFoundException.class, () -> filmStorage.get(createdFilm.getId()));
    }
}
