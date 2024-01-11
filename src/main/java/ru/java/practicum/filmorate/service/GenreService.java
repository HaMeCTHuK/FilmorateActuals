package ru.java.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.java.practicum.filmorate.model.Genre;
import ru.java.practicum.filmorate.storage.GenreStorage;

import java.util.List;

public class GenreService extends AbstractService<Genre> {

    private final GenreStorage genreStorage;
    @Autowired
    public GenreService(GenreStorage genreStorage) {     //через  австракт сервис к хранилищу storage
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAllGenres() {       //Для InMemory
        return genreStorage.getAll();
    }

    public Genre getGenre(long id) {          //Для InMemory
        return genreStorage.get(id);
    }

    @Override
    public void validate(Genre data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void validateParameter(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void validateParameters(Long id, Long otherId) {
        throw new UnsupportedOperationException();
    }
}
