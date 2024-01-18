package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.Genre;
import ru.java.practicum.filmorate.model.Mpa;
import ru.java.practicum.filmorate.storage.LikesStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikesDbStorage implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    // Метод для добавления лайка фильма от конкретного пользователя
    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO LIKES (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    // Метод для удаления лайка фильма от конкретного пользователя
    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sql = "DELETE FROM LIKES WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    // Метод для получения лайков для конкретного фильма
    @Override
    public int getLikesCountForFilm(Long filmId) {
        String sql = "SELECT COUNT(*) FROM LIKES WHERE film_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId);
        if (count == null) {
            return 0;
        }
        return count;
    }

    // Метод для получения списка фильмов, которые лайкнул пользователь
    @Override
    public List<Long> getAllFilmLikes(Long userId) {
        String sql = "SELECT film_id FROM LIKES WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, userId);
    }

    // Метод для получения списка фильмов с наибольшим количеством лайков
    @Override
    public List<Film> getPopularFilms(int count) {
        log.info("Отправляем запрос в БД для получения залайканых фильмов");
        String sql = "SELECT f.*, " +
                "m.rating_name AS mpa_rating_name, " +
                "f.mpa_rating_id, " +
                "g.genre_name, " +
                "fg.genre_id, " +
                "COUNT(l.film_id) AS like_count " +
                "FROM FILMS f " +
                "LEFT JOIN MPARating m ON f.mpa_rating_id = m.id " +
                "LEFT JOIN FILM_GENRE fg ON f.id = fg.film_id " +
                "LEFT JOIN GENRES g ON fg.genre_id = g.id " +
                "LEFT JOIN LIKES l ON f.id = l.film_id " +
                "GROUP BY f.id, m.rating_name, m.id, g.genre_name, fg.genre_id " +
                "ORDER BY like_count DESC " +
                "LIMIT ?;";

        List<Film> films = jdbcTemplate.query(sql, LikesDbStorage::createFilmWithLikes, count);

        return films;
    }

    private List<Genre> getGenresForFilm(Long filmId) {
        String genresSql = "SELECT g.* " +
                "FROM FILM_GENRE fg " +
                "JOIN GENRES g ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ?;";
        try {
            return jdbcTemplate.query(genresSql, LikesDbStorage::createGenre, filmId);
        } catch (DataNotFoundException e) {
            // Если жанров нет, возвращаем пустой список
            return Collections.emptyList();
        }
    }

    // Вспомогательный метод для создания объекта Genre из ResultSet
    public static Genre createGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .genreName(rs.getString("genre_name"))
                .build();
    }

    // Вспомогательный метод для создания объекта Mpa из ResultSet
    public static Mpa createMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("mpa_rating_id"))
                .ratingName(rs.getString("mpa_rating_name"))
                .build();
    }

    public static Film createFilmWithLikes(ResultSet rs, int rowNum) throws SQLException {
        log.info("Создаем объект Film после запроса к БД");

        Mpa mpa = createMpa(rs, rowNum);

        Long genreId = rs.getLong("genre_id");
        Genre genre = genreId != 0 ? createGenre(rs, rowNum) : null;

        Film film = Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .rating(rs.getInt("rating"))
                .likes(rs.getLong("like_count"))
                .mpa(mpa)
                .genres(genre != null ? Collections.singletonList(genre) : Collections.emptyList())
                .build();

        return film;
    }
}