package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.Genre;
import ru.java.practicum.filmorate.model.Mpa;
import ru.java.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;



// Метод для добавления нового фильма
  /*  @Override
    public Film create(Film film) {
        log.info("Отправляем данные для создания FILM в таблице");
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("id");

        Number id = simpleJdbcInsert.executeAndReturnKey(getParams(film)); //Получаем id из таблицы при создании

        film.setId(id.intValue());

        Mpa mpa = getMpaRating(film.getMpa());  // Получаем MPA из базы данных
        film.getMpa().setRatingName(mpa.getRatingName());  // Устанавливаем имя рейтинга MPA в объекте Film

        List<Genre> genres = getGenresForFilm(film.getId());  //// Получаем Genres
        film.setGenres(genres); //// Устанавливаем genres из базы данных


        log.info("Добавлен объект: " + film);

        return film;
    }*/
@Override
public Film create(Film film) {
    log.info("Отправляем данные для создания FILM в таблице");
    SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
            .withTableName("FILMS")
            .usingGeneratedKeyColumns("id");

    Number filmId = simpleJdbcInsert.executeAndReturnKey(getParams(film)); // Получаем id из таблицы при создании
    film.setId(filmId.intValue());

    // Добавляем информацию о жанрах в таблицу FILM_GENRE
    addGenresForFilm((long) filmId.intValue(), film.getGenres());

    Mpa mpa = getMpaRating(film.getMpa());  // Получаем MPA из базы данных
    film.getMpa().setRatingName(mpa.getRatingName());  // Устанавливаем имя рейтинга MPA в объекте Film

    //List<Genre> genres = getGenresForFilm(film.getId());  // Получаем Genres
    //film.setGenres(genres); // Устанавливаем genres из базы данных



    log.info("Добавлен объект: " + film);

    return film;
}

    // Метод для обновления существующего фильма
    @Override
    public Film update(Film film) {
        film.setMpa(getMpaRatingById(film.getMpa().getId()));
        String sql = "UPDATE FILMS " +
                "SET NAME=?, " +
                "DESCRIPTION=?, " +
                "RELEASE_DATE=?, " +
                "DURATION=?, " +
                "RATING=?, " +
                "MPA_RATING_ID=? " +
                "WHERE ID=?";
        log.info("Пытаемся обновить информацию о film");

        try {
            jdbcTemplate.update(sql, getParametersWithId(film));
        } catch (EmptyResultDataAccessException ex) {
            throw new DataNotFoundException("Данные о пользователе не найдены");
        }


        // Удаляем устаревшие жанры
        String deleteGenresSql = "DELETE FROM FILM_GENRE WHERE film_id = ?";
        jdbcTemplate.update(deleteGenresSql, film.getId());

        // Добавляем новые жанры (если они не дублируются)
        Set<Long> existingGenreIds = new HashSet<>();
        for (Genre genre : film.getGenres()) {
            if (!existingGenreIds.contains(genre.getId())) {
                existingGenreIds.add(genre.getId());
                jdbcTemplate.update("INSERT INTO FILM_GENRE (film_id, genre_id) VALUES (?, ?)", film.getId(), genre.getId());
            }
        }

        // Получаем обновленные жанры
        film.setGenres(getGenresForFilm(film.getId()));

        log.info("Обновлен объект: " + film);
        return film;
    }

    // Метод для получения списка всех фильмов
    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, m.rating_name AS mpa_rating_name, g.genre_name, fg.genre_id " +
                "FROM FILMS f " +
                "LEFT JOIN MPARating m ON f.mpa_rating_id = m.id " +
                "LEFT JOIN FILM_GENRE fg ON f.id = fg.film_id " +
                "LEFT JOIN GENRES g ON fg.genre_id = g.id";
        return jdbcTemplate.query(sql, FilmDbStorage::createFilm);
    }


/*    // Метод для получения конкретного фильма по его идентификатору
    @Override
    public Film get(Long id) {
        String sql = "SELECT f.*, m.rating_name AS mpa_rating_name " +
                "FROM FILMS f " +
                "LEFT JOIN MPARATING m ON f.mpa_rating_id = m.id WHERE f.id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, FilmDbStorage::createFilm, id);
        } catch (EmptyResultDataAccessException ex) {
            throw new DataNotFoundException("Данные о фильме не найдены");
        }
    }*/

    // Метод для получения конкретного фильма по его идентификатору
    @Override
    public Film get(Long id) {
        String sql = "SELECT * FROM FILMS " +
                "LEFT JOIN MPARating ON FILMS.mpa_rating_id = MPARating.id " +
                "LEFT JOIN FILM_GENRE ON FILMS.id = FILM_GENRE.film_id " +
                "LEFT JOIN GENRES ON FILM_GENRE.genre_id = GENRES.id " +
                "WHERE FILMS.id = ?";

        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql, id);

        List<Genre> genres = new ArrayList<>();
        if (!getGenresForFilm(id).isEmpty()) {
            genres = getGenresForFilm(id);
        }



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
                            .ratingName(resultSet.getString("rating_name"))
                            .build())
                    .genres(Collections.singletonList(
                            Genre.builder()
                                    .id(resultSet.getLong("id"))
                                    .genreName(resultSet.getString("genre_name"))
                                    .build()
                    ))
                    .build();

            film.setGenres(genres);


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



 /*
 // Метод для получения списка идентификаторов фильмов, которые лайкнул пользователь
    @Override
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
                film.getRating(), film.getMpa()};
    }

    // Вспомогательный метод для извлечения параметров для SQL-запросов с id
    protected Object[] getParametersWithId(Film film) {
        return new Object[]{
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() != null ? film.getReleaseDate().toString() : null,
                film.getDuration(),
                film.getRating(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()};
    }

    private static Map<String, Object> getParams(Film film) {

        Map<String, Object> params = Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate().toString(),
                "duration", film.getDuration(),
                "rating", film.getRating(),
                "mpa_rating_id", film.getMpa().getId(),
                "genres", film.getGenres()

        );
        return params;
    }

    // Вспомогательный метод для создания объекта Film из ResultSet
   /* public static Film createFilm(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = Mpa.builder()
                .id(rs.getLong("mpa_rating_id"))
                .ratingName(rs.getString("mpa_rating_name"))
                .build();

        Genre genre = Genre.builder()
                .id(rs.getLong("genre_id"))
                .genreName(rs.getString("genre_name"))
                .build();

        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .rating(rs.getInt("rating"))
                .mpa(mpa)
                .genres(Collections.singletonList(genre))
                .build();
    }*/
    public static Film createFilm(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = createMpa(rs, rowNum);

        /*Long genreId = rs.getLong("genre_id");
        Genre genre = genreId != 0 ? Genre.builder()
                .id(genreId)
                .genreName(rs.getString("genre_name"))
                .build() : null;*/

        Long genreId = rs.getLong("genre_id");
        Genre genre = genreId != 0 ? createGenre(rs, rowNum) : null;

        Film film = Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .rating(rs.getInt("rating"))
                .mpa(mpa)
                .genres(genre != null ? Collections.singletonList(genre) : Collections.emptyList())
                .build();

        return film;
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


    public List<Genre> getGenres(Long filmId) {
        String sqlQuery = "SELECT g.*" +
                "FROM FILM_GENRE fg " +
                "JOIN GENRES g ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ?;";
        //List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreDbStorage::createGenre, filmId);
        List<Genre> genres = jdbcTemplate.queryForList(sqlQuery, Genre.class, filmId);
        if (genres.isEmpty()) {
            return Collections.emptyList();
        }
        return genres;
    }


    // Метод для получения информации о GENRE по идентификатору фильма
    private List<Genre> getGenresForFilm(Long filmId) {
        String genresSql = "SELECT g.id as genre_id, g.genre_name " +
                "FROM FILM_GENRE fg " +
                "JOIN GENRES g ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ?";
        try {
            return jdbcTemplate.query(genresSql, FilmDbStorage::createGenre, filmId);
        } catch (DataNotFoundException e) {
            // Если жанров нет, возвращаем пустой список
            return Collections.emptyList();
        }
    }


    // Вспомогательный метод для получения информации о MPA рейтинге по его идентификатору
    private Mpa getMpaRatingById(long mpaRatingId) {
        String sqlQuery = "SELECT * FROM MPARating WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, MpaDbStorage::createMpa, mpaRatingId);
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

    // Метод для добавления информации о жанрах в таблицу FILM_GENRE
    private void addGenresForFilm(Long filmId, List<Genre> genres) {

        // Добавляем информацию о новых жанрах в таблицу FILM_GENRE
        if (genres != null && !genres.isEmpty()) {
            for (Genre genre : genres) {
                jdbcTemplate.update("INSERT INTO FILM_GENRE (film_id, genre_id) VALUES (?, ?)", filmId, genre.getId());

            }
        }
    }
}
