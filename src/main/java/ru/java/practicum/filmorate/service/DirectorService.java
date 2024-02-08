package ru.java.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.exception.IncorrectParameterException;
import ru.java.practicum.filmorate.model.Director;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.storage.DirectorStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DirectorService extends AbstractService<Director> {

    private final DirectorStorage storage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.storage = directorStorage;
        this.abstractStorage = directorStorage;
    }

    //Определяем какой метод сортировки получения списка фильмов будет использован
    public List<Film> getSortedDirectorList(Long directorId, String sortBy) {
        validateParameter(directorId);
        if (sortBy == null) {
            throw new IncorrectParameterException("Некорректное значение sortBy: null");
        }
        log.info("Получаем список фильмов режиссера отсортированных по {}", sortBy);
        List<Film> films = new ArrayList<>();

        if ("year".equals(sortBy)) {
            // Сортировка по году выпуска
            films = storage.getSortedDirectorListByYear(directorId);

        } else if ("likes".equals(sortBy)) {
            // Сортировка по количеству лайков
            films = storage.getSortedDirectorListByLikes(directorId);

        } else {
            throw new IncorrectParameterException("Некорректное задание сортировки");
        }

        return films;
    }

    @Override
    public Director create(Director data) {
        return abstractStorage.create(data);
    }

    @Override
    public Director update(Director data) {
        return abstractStorage.update(data);
    }

    @Override
    public List<Director> getAll() {
        return abstractStorage.getAll();
    }

    @Override
    public Director getData(Long id) {
        return abstractStorage.get(id);
    }

    @Override
    public void delete(Long id) {
        abstractStorage.delete(id);
    }

    @Override
    public void validateParameter(Long directorId) {
        if (directorId == null) {
            throw new IncorrectParameterException("Некорректные параметры поля, проверь null");
        }
        if (getData(directorId) == null) {
            throw new DataNotFoundException("Режисера с айди нет" + directorId);
        }
    }

    @Override
    public void validate(Director data) {
        if (data.getId() <= 0) {
            throw new IncorrectParameterException("некорректные данные режисера");
        }
    }

    @Override
    public void validateParameters(Long id, Long otherId) {
        throw new UnsupportedOperationException();
    }

}
