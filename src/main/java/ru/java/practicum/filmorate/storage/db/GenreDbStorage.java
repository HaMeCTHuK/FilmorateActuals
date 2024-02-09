package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Genre;
import ru.java.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    // Метод для получения списка всех жанров
    @Override
    public List<Genre> getAll() {
        String sqlQuery = "SELECT id as genre_id, genre_name FROM GENRES";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::createGenre);
    }

    // Метод для получения информации о жанре по его идентификатору
    @Override
    public Genre get(Long id) {
        String sqlQuery = "SELECT id as genre_id, genre_name FROM GENRES WHERE id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreDbStorage::createGenre, id);
        if (genres.size() != 1) {
            throw new DataNotFoundException("При получении жанра по id список не равен 1");
        }
        return genres.get(0);
    }

    // Вспомогательный метод для создания объекта Genre из ResultSet
    static Genre createGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    @Override
    // Метод для создания нового жанра GENRE в базе данных
    public Genre create(Genre genre) {
        String sql = "INSERT INTO GENRES (id, genre_name) VALUES (?, ?)";
        jdbcTemplate.update(sql, genre.getId(), genre.getName());
        return genre;
    }

    // Метод для обновления информации о GENRE в базе данных
    @Override
    public Genre update(Genre genre) {
        String sql = "UPDATE GENRES SET genre_name = ? WHERE id = ?";
        jdbcTemplate.update(sql, genre.getName(), genre.getId());
        return get(genre.getId());
    }

    // Метод для удаления GENRE по его идентификатору
    @Override
    public void delete(Long id) {
        String sqlQuery = "DELETE FROM GENRES WHERE id = ?";
        int affectedRows = jdbcTemplate.update(sqlQuery, id);
        if (affectedRows != 1) {
            throw new DataNotFoundException("При удалении GENRE по id количество удаленных строк не равно 1");
        }
    }

    // Метод для получения информации о GENRE по идентификатору фильма
    protected List<Genre> getGenresForFilm(Long filmId) {
        String genresSql = "SELECT g.id as genre_id, g.genre_name " +
                "FROM FILM_GENRE fg " +
                "JOIN GENRES g ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ?";
        try {
            return jdbcTemplate.query(genresSql, GenreDbStorage::createGenre, filmId);
        } catch (DataNotFoundException e) {
            // Если жанров нет, возвращаем пустой список
            return Collections.emptyList();
        }
    }
}
