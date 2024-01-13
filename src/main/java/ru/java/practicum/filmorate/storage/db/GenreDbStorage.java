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


    // Метод для получения списка всех жанров
    @Override
    public List<Genre> getAll() {
        String sqlQuery = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::createGenre);
    }


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
