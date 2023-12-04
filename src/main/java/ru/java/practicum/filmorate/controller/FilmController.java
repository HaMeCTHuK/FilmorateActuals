package ru.java.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.java.practicum.filmorate.exception.ValidationException;
import ru.java.practicum.filmorate.model.Film;



import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends BaseController<Film> {

    private static final  LocalDate LAST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public void validate(Film film) {

        if (film.getReleaseDate().isBefore(LAST_RELEASE_DATE)) {
            log.warn("Дата выпуска меньше 1895.12.28 : {}", film.getReleaseDate());
            throw new ValidationException("Дата выпуска меньше 1895.12.28");
        }
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {


        log.info("Пытаемся добавить фильм : {}", film);
        return super.create(film);
    }

    @PutMapping() //"/{id}"
    public Film updateFilm(@RequestBody @Valid Film film) {

        log.info("Пытаемся обновить фильм : {}", film);
        return super.update(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {

        log.info("Текущее количество фильмов: {}", super.getData().size());
        return super.getData();
    }
}
