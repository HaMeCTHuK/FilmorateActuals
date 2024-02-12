package ru.java.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.practicum.filmorate.model.Genre;
import ru.java.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService extends AbstractService<Genre> {

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.abstractStorage = genreStorage;
    }

    public List<Genre> getAllGenres() {
        return abstractStorage.getAll();
    }

    public Genre getGenre(long id) {
        return abstractStorage.get(id);
    }

    //В данной реализации метод не используется
    @Override
    public void validate(Genre data) {
        throw new UnsupportedOperationException("Метод не используется");
    }

    //В данной реализации метод не используется
    @Override
    public void validateParameter(Long id) {
        throw new UnsupportedOperationException("Метод не используется");
    }

    //В данной реализации метод не используется
    @Override
    public void validateParameters(Long id, Long otherId) {
        throw new UnsupportedOperationException("Метод не используется");
    }
}
