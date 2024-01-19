package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Genre;
import ru.java.practicum.filmorate.storage.GenreStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Test
    void getAll() {

        GenreStorage genreStorage = new GenreDbStorage(jdbcTemplate);

        List<Genre> genres = genreStorage.getAll();
        assertNotNull(genres);
        assertFalse(genres.isEmpty());
    }

    @Test
    void get() {

        GenreStorage genreStorage = new GenreDbStorage(jdbcTemplate);

        Genre genre = genreStorage.getAll().get(0);
        Genre recivedGenreById = genreStorage.get(genre.getId());
        assertNotNull(recivedGenreById);
        assertEquals(genre.getId(), recivedGenreById.getId());
    }

    @Test
    void getNonExistentGenre() {
        GenreStorage genreStorage = new GenreDbStorage(jdbcTemplate);

        assertThrows(DataNotFoundException.class, () -> genreStorage.get(-1L));
    }

    @Test
    void createGenre() {
        GenreStorage genreStorage = new GenreDbStorage(jdbcTemplate);

        Genre newGenre = Genre.builder().id(10L).name("New Genre").build();
        Genre createdGenre = genreStorage.create(newGenre);

        assertNotNull(createdGenre);
        assertEquals(newGenre.getName(), createdGenre.getName());
    }

    @Test
    void update() {
        GenreStorage genreStorage = new GenreDbStorage(jdbcTemplate);

        Genre existingGenre = genreStorage.getAll().get(0);
        String updatedName = "Updated Genre";

        existingGenre.setName(updatedName);
        Genre updatedGenre = genreStorage.update(existingGenre);

        assertEquals(existingGenre.getId(), updatedGenre.getId());
        assertEquals(updatedName, updatedGenre.getName());
    }

    @Test
    void delete() {
        GenreStorage genreStorage = new GenreDbStorage(jdbcTemplate);

        Genre newGenre = Genre.builder().id(7L).name("ToDelete Genre").build();
        Genre createdGenre = genreStorage.create(newGenre);

        assertNotNull(createdGenre);

        genreStorage.delete(createdGenre.getId());

        assertThrows(DataNotFoundException.class, () -> genreStorage.get(createdGenre.getId()));
    }
}
