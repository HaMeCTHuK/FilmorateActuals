package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    // Метод для добавления нового фильма
    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO FILMS (" +
                "name, " +
                "description, " +
                "release_date, " +
                "duration, " +
                "rating, " +
                "mpa_rating_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, getParameters(film));
        log.info("Добавлен объект: " + film);
        return film;
    }

    // Метод для обновления существующего фильма
    @Override
    public Film update(Film film) {
        String sql = "UPDATE FILMS SET " +
                "name=?, " +
                "description=?, " +
                "release_date=?, " +
                "duration=?, " +
                "rating=?, " +
                "mpa_rating_id=? " +
                "WHERE id=?";
        jdbcTemplate.update(sql, getParameters(film));
        log.info("Обновлен объект: " + film);
        return film;
    }

    // Метод для получения списка всех фильмов
    @Override
    public List<Film> getAll() {
        String sql = "SELECT * FROM FILMS" ;
        return jdbcTemplate.query(sql, FilmDbStorage::createFilm);
    }

    // Метод для получения конкретного фильма по его идентификатору
    @Override
    public Film get(Long id) {
        String sql = "SELECT * FROM FILMS WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, FilmDbStorage::createFilm, id);
    }

    // Метод для удаления фильма по его идентификатору
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM FILMS WHERE id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Удален объект с id=" + id);
    }

    // Метод для получения списка идентификаторов фильмов, которые лайкнул пользователь
   /* @Override
    public List<Long> getAllFilmLikes(Long userId) {
        String sqlQuery = "SELECT film_id FROM LIKES WHERE user_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, userId);
    }

    // Метод для добавления лайка к фильму от пользователя
    @Override
    public boolean addLike(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO LIKES (user_id, film_id) VALUES (?, ?)";
        int affectedRows = jdbcTemplate.update(sqlQuery, userId, filmId);
        return affectedRows > 0;
    }

    // Метод для удаления лайка к фильму от пользователя
    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE user_id = ? AND film_id = ?";
        int affectedRows = jdbcTemplate.update(sqlQuery, userId, filmId);
        return affectedRows > 0;
    }

    // Метод для получения списка популярных фильмов с заданным количеством
    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT * FROM FILMS ORDER BY rating DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::createFilm, count);
    }*/

    // Вспомогательный метод для извлечения параметров для SQL-запросов
    protected Object[] getParameters(Film film) {
        return new Object[]{film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRating(), film.getMpaRatingId(), film.getId()};
    }

    // Вспомогательный метод для создания объекта Film из ResultSet
    private static Film createFilm(ResultSet rs, int rowNum) throws SQLException {
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
