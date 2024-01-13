package ru.java.practicum.filmorate.storage.db;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Genre;
import ru.java.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDbStorage extends AbstractDbStorage<Genre> implements GenreStorage {

    private JdbcTemplate jdbcTemplate;


    @Override
    public List<Genre> getAll() {
        String sqlQuery = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sqlQuery, getRowMapper());

    }

    @Override
    public Genre get(Long id) {
        String sqlQuery = "SELECT * FROM GENRES WHERE id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, getRowMapper(), id);
        if (genres.size() != 1) {
            throw new DataNotFoundException("При получении жанра по id список не равен 1");
        }
        return genres.get(0);
    }

    @Override
    protected String getTableName() {
        return "GENRES";
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO GENRES (id, genre_name) VALUES (?, ?)";  //UnsupportedOperationException()
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE GENRES SET genre_name = ? WHERE id = ?";   //UnsupportedOperationException()
    }

    @Override
    protected Object[] getParameters(Genre data) {
        return new Object[]{data.getId(), data.getGenreName()};
    }

    @Override
    protected RowMapper<Genre> getRowMapper() {
        return GenreDbStorage::createGenre;
    }


    static Genre createGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("id"))
                .genreName(rs.getString("genre_name"))
                .build();
    }
}
