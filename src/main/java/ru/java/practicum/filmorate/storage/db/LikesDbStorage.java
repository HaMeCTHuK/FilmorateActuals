package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.model.Director;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.Genre;
import ru.java.practicum.filmorate.model.Mpa;
import ru.java.practicum.filmorate.storage.LikesStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikesDbStorage implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final DirectorDbStorage directorDbStorage;

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
    public long getLikesCountForFilm(Long filmId) {
        String sql = "SELECT COUNT(*) FROM LIKES WHERE film_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, filmId);
        return count != null ? count : 0L;
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
                "GROUP_CONCAT(DISTINCT CONCAT(g.id, ':', g.genre_name)) AS genres, " +
                "GROUP_CONCAT(DISTINCT CONCAT(d.id, ':', d.director_name)) AS directors, " +
                "COUNT(l.film_id) AS like_count " +
                "FROM FILMS f " +
                "LEFT JOIN MPARating m ON f.mpa_rating_id = m.id " +
                "LEFT JOIN FILM_GENRE fg ON f.id = fg.film_id " +
                "LEFT JOIN GENRES g ON fg.genre_id = g.id " +
                "LEFT JOIN LIKES l ON f.id = l.film_id " +
                "LEFT JOIN FILM_DIRECTOR fd ON f.id = fd.film_id " +
                "LEFT JOIN DIRECTORS d ON fd.director_id = d.id " +
                "GROUP BY f.id, m.rating_name, m.id " +
                "ORDER BY like_count DESC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, LikesDbStorage::createFilmWithLikes, count);

        return films;
    }

    public List<Genre> getGenresForFilm(String sql) {

        return jdbcTemplate.query(sql, rs -> {
            String genresString = rs.next() ? rs.getString("genres") : "";
            if (genresString.isEmpty() || genresString.equals(":") ) {
                return Collections.emptyList();
            }
            return Arrays.stream(genresString.split(","))
                    .map(genre -> {
                        String[] parts = genre.split(":");
                        return new Genre().builder().id(Long.parseLong(parts[0])).name(parts[1]).build();
                    })
                    .collect(Collectors.toList());
        });
    }

    public List<Director> getDirectorsForFilm(String sql) {
        Map<Long, List<Genre>> filmGenresMap = new HashMap<>();

        return jdbcTemplate.query(sql, rs -> {
            String directorsString = rs.next() ? rs.getString("directors") : "";
            if (directorsString.isEmpty() || directorsString.equals(":")) {
                return Collections.emptyList();
            }
            return Arrays.stream(directorsString.split(","))
                    .map(director -> {
                        String[] parts = director.split(":");
                        //return new Director(Long.parseLong(parts[0]), parts[1]);
                        return new Director().builder().id(Long.parseLong(parts[0])).name(parts[1]).build();
                    })
                    .collect(Collectors.toList());
        });
    }

    // Вспомогательный метод для создания объекта Genre из ResultSet
    public static Genre createGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    // Вспомогательный метод для создания объекта Director из ResultSet
    static Director createDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("director_name"))
                .build();
    }

    // Вспомогательный метод для создания объекта Mpa из ResultSet
    public static Mpa createMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("mpa_rating_id"))
                .name(rs.getString("mpa_rating_name"))
                .build();
    }

    public static Film createFilmWithLikes(ResultSet rs, int rowNum) throws SQLException {
        log.info("Создаем объект Film после запроса к БД");

        Mpa mpa = createMpa(rs, rowNum);

        // Извлекаем строку с жанрами и режиссерами
        String genresString = rs.getString("genres");
        String directorsString = rs.getString("directors");

        // Создаем список жанров
        List<Genre> genres = new ArrayList<>();
        if (genresString != null && !genresString.isEmpty() && !genresString.equals(":")) {
            String[] genreParts = genresString.split(",");
            for (String part : genreParts) {
                String[] parts = part.split(":");
                Genre genre = new Genre().builder().id(Long.parseLong(parts[0])).name(parts[1]).build();
                genres.add(genre);
            }
        }

        // Создаем список режиссеров
        List<Director> directors = new ArrayList<>();
        if (directorsString != null && !directorsString.isEmpty() && !directorsString.equals(":")) {
            String[] directorParts = directorsString.split(",");
            for (String part : directorParts) {
                String[] parts = part.split(":");
                Director director = new Director().builder().id(Long.parseLong(parts[0])).name(parts[1]).build();
                directors.add(director);
            }
        }

        // Создаем объект Film
        Film film = Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .rating(rs.getInt("rating"))
                .likes(rs.getLong("like_count"))
                .mpa(mpa)
                .genres(genres)
                .directors(directors)
                .build();

        return film;
    }
}