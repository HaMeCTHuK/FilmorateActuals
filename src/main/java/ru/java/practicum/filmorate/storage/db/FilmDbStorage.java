package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.storage.FilmStorage;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmDbStorage extends AbstractDbStorage<Film> implements FilmStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Long> getAllFilmLikes(Long userId) {
        return null;
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        return false;
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        return false;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return null;
    }
}
