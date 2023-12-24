package ru.java.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        log.info("Пытаемся добавить фильм : {}", film);
        return filmService.create(film);
    }

    @PutMapping()
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("Пытаемся обновить фильм : {}", film);
        return filmService.update(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Текущее количество фильмов: {}", filmService.getAll().size());
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilm(@RequestBody @PathVariable Long id) {
        log.info("Получаем объект по id: {}", id);
        return filmService.getData(id);
    }

    //PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.

    @PutMapping("/{id}/like/{userId}")
    public boolean likeFilm(@RequestBody @PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь с id: {} пытается поставить лайк фильму с айди {}", id, userId);
        return filmService.addLike(id, userId);
    }

    //DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.

    @DeleteMapping("/{id}/like/{userId}")
    public boolean removeFilmLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь с id: {} пытается удалить лайк у фильма с айди {}", id, userId);
        return filmService.deleteLike(id, userId);
    }

    //GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано, верните первые 10.

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Пытаемся получить самые залайканые фильмы количеством: {} шт.", count);
        return filmService.getPopularFilms(count);
    }

}
