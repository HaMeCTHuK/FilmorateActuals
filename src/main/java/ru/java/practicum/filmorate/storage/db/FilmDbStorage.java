package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmDbStorage extends AbstractDbStorage<Film> implements FilmStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Long> getAllFilmLikes(Long userId) {
        String sqlQuery = "SELECT film_id FROM LIKES WHERE user_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, userId);
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO LIKES (user_id, film_id) VALUES (?, ?)";
        int affectedRows = jdbcTemplate.update(sqlQuery, userId, filmId);
        return affectedRows > 0;
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE user_id = ? AND film_id = ?";
        int affectedRows = jdbcTemplate.update(sqlQuery, userId, filmId);
        return affectedRows > 0;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT * FROM FILMS ORDER BY rating DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, getRowMapper(), count);
    }

    @Override
    protected String getTableName() {
        return "FILMS";
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO FILMS (name, description, release_date, duration, rating, mpa_rating_id) VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE FILMS SET name=?, description=?, release_date=?, duration=?, rating=?, mpa_rating_id=? WHERE id=?";
    }

    @Override
    protected Object[] getParameters(Film data) {
        return new Object[]{data.getName(), data.getDescription(), data.getReleaseDate(), data.getDuration(),
                data.getRating(), data.getMpaRatingId(), data.getId()};
    }

    @Override
    protected RowMapper<Film> getRowMapper() {
        return (rs, rowNum) -> mapFilmFromResultSet(rs);
    }

    private static Film mapFilmFromResultSet(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .rating(rs.getInt("rating"))
                .mpaRatingId(rs.getInt("mpa_rating_id"))
                .build();
    }
}


















