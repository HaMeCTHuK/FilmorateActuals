package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Director;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.Mpa;
import ru.java.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DirectorDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private DirectorDbStorage directorDbStorage;
    private LikesDbStorage likeStorage;
    private FilmDbStorage filmStorage;

    @BeforeEach
    void init() {
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        directorDbStorage = new DirectorDbStorage(jdbcTemplate);
        likeStorage = new LikesDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, likeStorage, directorDbStorage, genreDbStorage);
    }

    @Test
    void getSortedDirectorListByYear() {

        //Создаем режиссера и добавляем в бд
        Director director = Director.builder().id(1).name("Boss").build();
        Director createdDirector = directorDbStorage.create(director);

        // Подготавливаем данные для теста
        Film newFilm = new Film(
                "testFilm",
                "description",
                LocalDate.of(1999,2,22),
                100,
                0,
                new Mpa(),
                0L);

        newFilm.getMpa().setId(2);
        newFilm.getDirectors().add(createdDirector);
        Film createdFilm = filmStorage.create(newFilm);

        Film newFilm2 = new Film(
                "testFilm2",
                "description2",
                LocalDate.of(2000,2,22),
                100,
                0,
                new Mpa(),
                0L);

        newFilm2.getMpa().setId(3);
        newFilm2.getDirectors().add(createdDirector);
        Film createdFilm2 = filmStorage.create(newFilm2);

        List<Film> sortedListByYear = directorDbStorage.getSortedDirectorListByYear(createdDirector.getId());
        //Проверяем что список не пуст
        assertThat(sortedListByYear).isNotNull();
        //Проверяем сортировку по году
        assertEquals(sortedListByYear.get(0).getReleaseDate(), LocalDate.of(1999,2,22));
    }

    @Test
    void getSortedDirectorListByLikes() {

        //Создаем режиссера и добавляем в бд
        Director director = Director.builder().id(1).name("Boss").build();
        Director createdDirector = directorDbStorage.create(director);

        // Подготавливаем данные для теста
        Film newFilm = new Film(
                "testFilm",
                "description",
                LocalDate.of(1999,2,22),
                100,
                0,
                new Mpa(),
                0L);

        newFilm.getMpa().setId(2);
        newFilm.getDirectors().add(createdDirector);
        Film createdFilm = filmStorage.create(newFilm);

        Film newFilm2 = new Film(
                "testFilm2",
                "description2",
                LocalDate.of(2000,2,22),
                100,
                0,
                new Mpa(),
                0L);

        newFilm2.getMpa().setId(3);
        newFilm2.getDirectors().add(createdDirector);
        Film createdFilm2 = filmStorage.create(newFilm2);

        User newUser = new User(
                "user@email.ru",
                "vanya123",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        User createdUser = userStorage.create(newUser);

        //добавляем лайки
        likeStorage.addLike(createdFilm.getId(),createdUser.getId());

        List<Film> sortedListByLikes = directorDbStorage.getSortedDirectorListByLikes(createdDirector.getId());
        //Проверяем что список не пуст
        assertThat(sortedListByLikes).isNotNull();
        //Проверяем сортировку по году
        assertEquals(sortedListByLikes.get(0).getLikes(), 1L);
    }

    @Test
    void createDirector() {

        List<Director> directors = new ArrayList<>();
        Director director = Director.builder().id(1).name("Boss").build();
        directors.add(director);

        // Записываем режиссера в базу данных
        Director createdDirector = directorDbStorage.create(director);

        // Проверяем, что режиссер успешно создан
        assertThat(createdDirector).isNotNull();
        assertThat(directorDbStorage.get(createdDirector.getId())).isNotNull();
        assertThat(createdDirector.getName()).isEqualTo(director.getName());
    }

    @Test
    void updateDirector() {

        List<Director> directors = new ArrayList<>();
        Director director = Director.builder().id(1).name("Boss").build();
        directors.add(director);
        Director createdDirector = directorDbStorage.create(director);
        String updatedName = "Updated DirectorName";

        createdDirector.setName(updatedName);
        Director updatedDirector = directorDbStorage.update(createdDirector);

        assertEquals(createdDirector.getId(), updatedDirector.getId());
        assertEquals(updatedName, updatedDirector.getName());

    }

    @Test
    void getAllDirectors() {

        Director director = Director.builder().id(1).name("Boss").build();
        Director createdDirector = directorDbStorage.create(director);
        List<Director> directors = directorDbStorage.getAll();

        assertNotNull(directors);
        assertFalse(directors.isEmpty());
    }

    @Test
    void getDirector() {

        Director director = Director.builder().id(1).name("Boss").build();
        Director createdDirector = directorDbStorage.create(director);
        Director recivedDirectorById = directorDbStorage.get(createdDirector.getId());

        assertNotNull(recivedDirectorById);
        assertEquals(createdDirector.getId(), recivedDirectorById.getId());
    }

    @Test
    void deleteDirector() {

        Director director = Director.builder().id(1).name("Boss").build();
        Director createdDirector = directorDbStorage.create(director);

        assertNotNull(createdDirector);

        directorDbStorage.delete(createdDirector.getId());

        assertThrows(DataNotFoundException.class, () -> directorDbStorage.get(createdDirector.getId()));
    }
}