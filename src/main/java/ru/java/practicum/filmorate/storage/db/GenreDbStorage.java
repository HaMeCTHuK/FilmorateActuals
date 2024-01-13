package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Genre;
import ru.java.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

/*    // Метод для создания нового жанра в базе данных
    @Override
    public Genre create(Genre genre) {
        String sql = "INSERT INTO GENRES (genre_name) VALUES (?)";
        jdbcTemplate.update(sql, genre.getGenreName());
        return get(genre.getId());
    }

    // Метод для обновления информации о жанре в базе данных
    @Override
    public Genre update(Genre data) {
        String sql = "UPDATE GENRES SET genre_name = ? WHERE id = ?";
        jdbcTemplate.update(sql, data.getGenreName(), data.getId());
        return get(data.getId());
    }*/

    // Метод для получения списка всех жанров
    @Override
    public List<Genre> getAll() {
        String sqlQuery = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::createGenre);
    }

/*    // Метод для удаления жанра по его идентификатору
    @Override
    public void delete(Long id) {
        String sqlQuery = "DELETE FROM GENRES WHERE id = ?";
        int affectedRows = jdbcTemplate.update(sqlQuery, id);
        if (affectedRows != 1) {
            throw new DataNotFoundException("При удалении жанра по id количество удаленных строк не равно 1");
        }
    }*/

    // Метод для получения информации о жанре по его идентификатору
    @Override
    public Genre get(Long id) {
        String sqlQuery = "SELECT * FROM GENRES WHERE id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreDbStorage::createGenre, id);
        if (genres.size() != 1) {
            throw new DataNotFoundException("При получении жанра по id список не равен 1");
        }
        return genres.get(0);
    }

    // Вспомогательный метод для извлечения параметров жанра из ResultSet
    protected Object[] getParameters(Genre data) {
        return new Object[]{data.getId(), data.getGenreName()};
    }

    // Вспомогательный метод для создания объекта Genre из ResultSet
    static Genre createGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("id"))
                .genreName(rs.getString("genre_name"))
                .build();
    }
}
