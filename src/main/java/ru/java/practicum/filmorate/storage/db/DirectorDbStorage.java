package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Director;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.storage.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;

    // Метод получения списка фильмов режисера с сортировкой по году
    @Override
    public List<Film> getSortedDirectorListByYear(Long directorId) {
        String query = "SELECT f.*, " +
                "m.rating_name AS mpa_rating_name, " +
                "m.id AS mpa_rating_id, " +
                "GROUP_CONCAT(DISTINCT CONCAT(g.id, ':', g.genre_name)) AS genres, " +
                "GROUP_CONCAT(DISTINCT CONCAT(d.id, ':', d.director_name)) AS directors, " +
                "COUNT(l.film_id) AS like_count " +
                "FROM films f " +
                "JOIN film_director fd ON f.id = fd.film_id " +
                "LEFT JOIN MPARating m ON f.mpa_rating_id = m.id " +
                "LEFT JOIN FILM_GENRE fg ON f.id = fg.film_id " +
                "LEFT JOIN GENRES g ON fg.genre_id = g.id " +
                "LEFT JOIN DIRECTORS d ON fd.director_id = d.id " +
                "LEFT JOIN LIKES l ON f.id = l.film_id " +
                "WHERE fd.director_id = ? " +
                "GROUP BY f.id " +
                "ORDER BY f.release_date";

        List<Film> films = jdbcTemplate.query(query, FilmDbStorage::createFilm, directorId);

        return films;
    }

    // Метод получения списка фильмов режисера с сортировкой по лайкам
    @Override
    public List<Film> getSortedDirectorListByLikes(Long directorId) {
        String query = "SELECT f.*, " +
                "COUNT(likes.film_id) AS like_count, " +
                "m.rating_name AS mpa_rating_name, " +
                "m.id AS mpa_rating_id, " +
                "GROUP_CONCAT(DISTINCT CONCAT(g.id, ':', g.genre_name)) AS genres, " +
                "GROUP_CONCAT(DISTINCT CONCAT(d.id, ':', d.director_name)) AS directors " +
                "FROM films f " +
                "LEFT JOIN likes ON f.id = likes.film_id " +
                "LEFT JOIN MPARating m ON f.mpa_rating_id = m.id " +
                "LEFT JOIN FILM_GENRE fg ON f.id = fg.film_id " +
                "LEFT JOIN GENRES g ON fg.genre_id = g.id " +
                "LEFT JOIN FILM_DIRECTOR fd ON f.id = fd.film_id " +
                "LEFT JOIN DIRECTORS d ON fd.director_id = d.id " +
                "WHERE fd.director_id = ? " +
                "GROUP BY f.id " +
                "ORDER BY like_count DESC";
        List<Film> films = jdbcTemplate.query(query, FilmDbStorage::createFilm, directorId);

        return films;
    }

    // Метод создания режиссера в базе данных
    @Override
    public Director create(Director director) {
        log.info("Отправляем данные для создания DIRECTOR в таблице");
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("DIRECTORS")
                .usingGeneratedKeyColumns("id");

        Number id = simpleJdbcInsert.executeAndReturnKey(Map.of("director_name", director.getName()));

        director.setId(id.intValue());
        log.info("Добавлен режиссер: {} {}", director.getId(), director.getName());
        return director;
    }

    // Метод обновления режиссера в базе данных
    @Override
    public Director update(Director director) {
        String sql = "UPDATE DIRECTORS SET director_name = ? WHERE id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return get(director.getId());
    }

    // Метод получения всех режиссеров из базы данных
    @Override
    public List<Director> getAll() {
        String sqlQuery = "SELECT * FROM DIRECTORS";
        return jdbcTemplate.query(sqlQuery, DirectorDbStorage::createDirector);
    }

    // Метод получения режиссера по идентификатору из базы данных
    @Override
    public Director get(Long id) {
        String sqlQuery = "SELECT * FROM DIRECTORS WHERE id = ?";
        List<Director> directors = jdbcTemplate.query(sqlQuery, DirectorDbStorage::createDirector, id);
        if (directors.size() != 1) {
            throw new DataNotFoundException("При получении жанра по id список не равен 1");
        }
        return directors.get(0);
    }

    // Метод удаления режиссера из базы данных
    @Override
    public void delete(Long id) {

        // Удаляем режиссера из таблицы DIRECTORS
        String deleteDirectorQuery = "DELETE FROM DIRECTORS WHERE id = ?";
        int affectedRows = jdbcTemplate.update(deleteDirectorQuery, id);

        if (affectedRows != 1) {
            throw new DataNotFoundException("При удалении режисера по id количество удаленных строк не равно 1");
        }

        log.info("Удален объект с id = " + id);
    }

    // Вспомогательный метод для создания объекта Director из ResultSet
    static Director createDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("director_name"))
                .build();
    }

    // Метод для получения информации о DIRECTORS по идентификатору фильма
    protected List<Director> getDirectorsForFilm(Long filmId) {
        String directorsSql = "SELECT d.id as director_id, d.director_name " +
                "FROM FILM_DIRECTOR fd " +
                "JOIN DIRECTORS d ON fd.director_id = d.id " +
                "WHERE fd.film_id = ?";
        try {
            return jdbcTemplate.query(directorsSql, DirectorDbStorage::createDirector, filmId);
        } catch (DataNotFoundException e) {
            // Если режиссеров нет, возвращаем пустой список
            return Collections.emptyList();
        }
    }

    // Метод для получения лайков для конкретного фильма
    public Long getLikesCountForFilm(Long filmId) {
        String sql = "SELECT COUNT(*) FROM LIKES WHERE film_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, filmId);
        if (count == null) {
            return 0L;
        }
        return count;
    }
}
