package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Director;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.Genre;
import ru.java.practicum.filmorate.model.Mpa;
import ru.java.practicum.filmorate.storage.FilmStorage;
import ru.java.practicum.filmorate.storage.LikesStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final LikesStorage likesStorage;
    private final DirectorDbStorage directorDbStorage;
    private final GenreDbStorage genreDbStorage;

    // Метод для добавления нового фильма
    @Override
    public Film create(Film film) {
        log.info("Отправляем данные для создания FILM в таблице");
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("id");

        Number filmId = simpleJdbcInsert.executeAndReturnKey(getParams(film)); // Получаем id из таблицы при создании
        film.setId(filmId.longValue());

        // Добавляем информацию о жанрах в таблицу FILM_GENRE
        addGenresForFilm(filmId.longValue(), film.getGenres());
        // Добавляем информацию о жанрах в таблицу FILM_DIRECTORS
        addDirectorForFilm(filmId.longValue(), film.getDirectors());

        // Заполняем жанры, режиссеров, лайки
        film.setGenres(genreDbStorage.getGenresForFilm(filmId.longValue()));
        film.setDirectors(directorDbStorage.getDirectorsForFilm(filmId.longValue()));
        film.setLikes(likesStorage.getLikesCountForFilm(filmId.longValue()));


        Mpa mpa = getMpaRating(film.getMpa());  // Получаем MPA из базы данных
        film.getMpa().setName(mpa.getName());  // Устанавливаем имя рейтинга MPA в объекте Film
        log.info("Добавлен объект: " + film);
        return film;
    }

    // Метод для обновления существующего фильма
    @Override
    public Film update(Film film) {
        //film.setMpa(getMpaRatingById(film.getMpa().getId()));
        String sql = "UPDATE FILMS " +
                "SET NAME=?, " +
                "DESCRIPTION=?, " +
                "RELEASE_DATE=?, " +
                "DURATION=?, " +
                "RATING=?, " +
                "MPA_RATING_ID=? " +
                "WHERE ID=?";
        log.info("Пытаемся обновить информацию о film");
        int rowsUpdated = jdbcTemplate.update(sql, getParametersWithId(film));
        if (rowsUpdated == 0) {
            log.info("Данные о фильме не найдены");
            throw new DataNotFoundException("Данные о фильме не найдены");
        }

        // Удаляем устаревшие жанры
        String deleteGenresSql = "DELETE FROM FILM_GENRE WHERE film_id = ?";
        jdbcTemplate.update(deleteGenresSql, film.getId());

        // Добавляем новые жанры (если они не дублируются)
        Set<Long> existingGenreIds = new HashSet<>();
        for (Genre genre : film.getGenres()) {
            if (!existingGenreIds.contains(genre.getId())) {
                existingGenreIds.add(genre.getId());
                jdbcTemplate.update("INSERT INTO FILM_GENRE (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }

        // Удаляем устаревших режисеров
        String deleteDirectorSql = "DELETE FROM FILM_DIRECTOR WHERE film_id = ?";
        jdbcTemplate.update(deleteDirectorSql, film.getId());

        // Добавляем новых режисеров (если они не дублируются)
        Set<Long> existingDirectorIds = new HashSet<>();
        for (Director director : film.getDirectors()) {
            if (!existingDirectorIds.contains(director.getId())) {
                existingDirectorIds.add(director.getId());
                jdbcTemplate.update("INSERT INTO FILM_DIRECTOR (film_id, director_id) VALUES (?, ?)",
                        film.getId(), director.getId());
            }
        }

        // Получаем обновленные жанры
        film.setGenres(genreDbStorage.getGenresForFilm(film.getId()));
        // Получаем обновленных режиссеров
        film.setDirectors(directorDbStorage.getDirectorsForFilm(film.getId()));
        //Получаем обновленные лайки
        film.setLikes((likesStorage.getLikesCountForFilm(film.getId())));

        log.info("Обновлен объект: " + film);
        return film;
    }

    // Метод для получения списка всех фильмов
    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, m.rating_name AS mpa_rating_name " +
                "FROM FILMS f " +
                "LEFT JOIN MPARating m ON f.mpa_rating_id = m.id";
        List<Film> films = jdbcTemplate.query(sql, FilmDbStorage::createFilm);

        // Добавляем жанры, лайки и режиссеров к каждому фильму
        for (Film film : films) {
            List<Genre> genres = genreDbStorage.getGenresForFilm(film.getId());
            List<Director> directors = directorDbStorage.getDirectorsForFilm(film.getId());
            film.setLikes((likesStorage.getLikesCountForFilm(film.getId())));
            film.setGenres(genres);
            film.setDirectors(directors);
        }

        return films;
    }

    // Метод для получения конкретного фильма по его идентификатору
    @Override
    public Film get(Long id) {
        String sql = "SELECT * FROM FILMS " +
                "LEFT JOIN MPARating ON FILMS.mpa_rating_id = MPARating.id " +
                "LEFT JOIN FILM_GENRE ON FILMS.id = FILM_GENRE.film_id " +
                "LEFT JOIN GENRES ON FILM_GENRE.genre_id = GENRES.id " +
                "LEFT JOIN FILM_DIRECTOR ON FILMS.id = FILM_DIRECTOR.film_id " +
                "LEFT JOIN DIRECTORS ON FILM_DIRECTOR.director_id = DIRECTORS.id " +
                "WHERE FILMS.id = ?";

        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql, id);

        if (resultSet.next()) {
            Film film = Film.builder()
                    .id(resultSet.getLong("id"))
                    .name(resultSet.getString("name"))
                    .description(resultSet.getString("description"))
                    .releaseDate(Objects.requireNonNull(resultSet.getDate("release_date")).toLocalDate())
                    .duration(resultSet.getInt("duration"))
                    .rating(resultSet.getInt("rating"))
                    .mpa(Mpa.builder()
                            .id(resultSet.getLong("mpa_rating_id"))
                            .name(resultSet.getString("rating_name"))
                            .build())
                    .genres(Collections.singletonList(
                            Genre.builder()
                                    .id(resultSet.getLong("id"))
                                    .name(resultSet.getString("genre_name"))
                                    .build()))
                    .directors(Collections.singletonList(
                            Director.builder()
                                    .id(resultSet.getLong("id"))
                                    .name(resultSet.getString("director_name"))
                                    .build()))
                    .build();

            List<Genre> genres = genreDbStorage.getGenresForFilm(id);
            List<Director> directors = directorDbStorage.getDirectorsForFilm(id);
            film.setLikes((likesStorage.getLikesCountForFilm(film.getId())));

            film.setGenres(genres);
            film.setDirectors(directors);
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return film;
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new DataNotFoundException("Фильм не найден.");
        }
    }

    // Метод для удаления фильма по его идентификатору
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM FILMS WHERE id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Удален объект с id=" + id);
    }

    // Вспомогательный метод для извлечения параметров для SQL-запросов с id
    protected Object[] getParametersWithId(Film film) {
        return new Object[]{
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() != null ? film.getReleaseDate().toString() : null,
                film.getDuration(),
                film.getRating(),
                film.getMpa() != null ? film.getMpa().getId() : Collections.emptyList(),
                film.getId()};
    }

    // Вспомогательный метод для извлечения параметров для SQL-запросов
    private static Map<String, Object> getParams(Film film) {

        Map<String, Object> params = Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate().toString(),
                "duration", film.getDuration(),
                "rating", film.getRating(),
                "mpa_rating_id", film.getMpa().getId(),
                "genres", film.getGenres(),
                "directors", film.getDirectors()

        );
        return params;
    }

    // Вспомогательный метод для создания объекта Film из ResultSet
    public static Film createFilm(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = createMpa(rs, rowNum);

        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .rating(rs.getInt("rating"))
                .mpa(mpa)
                .build();
    }

    // Метод для получения информации о MPA рейтинге по его идентификатору
    @Override
    public Mpa getMpaRating(Mpa mpa) {
        String sqlQuery = "SELECT * FROM MPARating WHERE id = ?";
        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, MpaDbStorage::createMpa, mpa.getId());
        if (mpas.size() != 1) {
            throw new DataNotFoundException("При получении MPA по id список не равен 1");
        }
        return mpas.get(0);
    }

    // Вспомогательный метод для получения информации о MPA рейтинге по его идентификатору
    private Mpa getMpaRatingById(long mpaRatingId) {
        String sqlQuery = "SELECT * FROM MPARating WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, MpaDbStorage::createMpa, mpaRatingId);
    }

    // Вспомогательный метод для создания объекта Mpa из ResultSet
    public static Mpa createMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("mpa_rating_id"))
                .name(rs.getString("mpa_rating_name"))
                .build();
    }

    // Метод для добавления информации о жанрах в таблицу FILM_GENRE
    private void addGenresForFilm(Long filmId, List<Genre> genres) {

        // Добавляем информацию о новых жанрах в таблицу FILM_GENRE
        if (genres != null && !genres.isEmpty()) {
            for (Genre genre : genres) {
                jdbcTemplate.update("INSERT INTO FILM_GENRE (film_id, genre_id) VALUES (?, ?)",
                        filmId, genre.getId());
            }
        }
    }

    // Метод для добавления информации о жанрах в таблицу FILM_DIRECTOR
    private void addDirectorForFilm(Long filmId, List<Director> directors) {

        // Добавляем информацию о новых жанрах в таблицу FILM_DIRECTOR
        if (directors != null && !directors.isEmpty()) {
            for (Director director : directors) {
                jdbcTemplate.update("INSERT INTO FILM_DIRECTOR (film_id, director_id) VALUES (?, ?)",
                        filmId, director.getId());
            }
        }
    }

    //Метод для получения списка самых популярных фильмов указанного жанра за нужный год.
    @Override
    public List<Film> getPopularWithYearForYear(int limit, long genreId, int year) {
        String popWithYearFilmsSql = "SELECT f.*, m.rating_name AS mpa_rating_name " +
                "FROM FILMS f " +
                "LEFT JOIN MPARating m ON f.mpa_rating_id = m.id " +
                "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(popWithYearFilmsSql, FilmDbStorage::createFilm, year, limit);

        // Добавляем жанры и режиссеров к каждому фильму
        for (Film film : films) {
            List<Genre> genres = genreDbStorage.getGenresForFilm(film.getId());
            List<Director> directors = directorDbStorage.getDirectorsForFilm(film.getId());
            film.setLikes((likesStorage.getLikesCountForFilm(film.getId())));
            film.setGenres(genres);
            film.setDirectors(directors);

        }
        return films;
    }

    // Метод для получения списка самых популярных фильмов указанного жанра
    @Override
    public List<Film> getPopularWithGenre(int limit, Long genreId) {
        String popWithGenreFilmsSql = "SELECT f.*, m.rating_name AS mpa_rating_name " +
                "FROM FILMS f " +
                "LEFT JOIN MPARating m ON f.mpa_rating_id = m.id " +
                "LEFT JOIN FILM_GENRE fg ON f.id = fg.film_id " +
                "WHERE fg.genre_id = ? " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(popWithGenreFilmsSql, FilmDbStorage::createFilm, genreId, limit);

        // Добавляем жанры и режиссеров к каждому фильму
        for (Film film : films) {
            List<Genre> genres = genreDbStorage.getGenresForFilm(film.getId());
            List<Director> directors = directorDbStorage.getDirectorsForFilm(film.getId());
            film.setLikes((likesStorage.getLikesCountForFilm(film.getId())));
            film.setDirectors(directors);
            film.setGenres(genres);
        }
        return films;
    }

    // Метод для получения списка самых популярных фильмов за нужный год.
    @Override
    public List<Film> getPopularWithYear(int limit, Integer year) {
        String popWithYearFilmsSql = "SELECT f.*, m.rating_name AS mpa_rating_name " +
                "FROM FILMS f " +
                "LEFT JOIN MPARating m ON f.mpa_rating_id = m.id " +
                "WHERE EXTRACT(YEAR FROM f.release_date) = ?" +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(popWithYearFilmsSql, FilmDbStorage::createFilm, year, limit);

        // Добавляем жанры и режиссеров к каждому фильму
        for (Film film : films) {
            List<Genre> genres = genreDbStorage.getGenresForFilm(film.getId());
            List<Director> directors = directorDbStorage.getDirectorsForFilm(film.getId());
            film.setLikes((likesStorage.getLikesCountForFilm(film.getId())));
            film.setGenres(genres);
            film.setDirectors(directors);
        }
        return films;
    }

}

