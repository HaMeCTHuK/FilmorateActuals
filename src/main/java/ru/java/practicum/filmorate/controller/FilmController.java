package ru.java.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.service.DirectorService;
import ru.java.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final DirectorService directorService;

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
        List<Film> allFilms = filmService.getAll();
        log.info("Текущее количество фильмов: {}", allFilms.size());
        return allFilms;
    }

    @GetMapping("/{id}")
    public Film getFilm(@RequestBody @PathVariable Long id) {
        log.info("Получаем объект по id: {}", id);
        return filmService.getData(id);
    }

    //PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@RequestBody @PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь с id: {} пытается поставить лайк фильму с айди {}", id, userId);
        filmService.addLike(id, userId);
    }

    //DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.

    @DeleteMapping("/{id}/like/{userId}")
    public void removeFilmLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь с id: {} пытается удалить лайк у фильма с айди {}", id, userId);
        filmService.deleteLike(id, userId);
    }

    //GET /films/director/{directorId}?sortBy=[year,likes]
    //Возвращает список фильмов режиссера отсортированных по количеству лайков или году выпуска.
    @GetMapping("/director/{directorId}")
    public List<Film> getSortedDirectorList(@PathVariable Long directorId,
                                            @RequestParam(required = false) String sortBy) {
        log.info("Возвращает список фильмов режиссера отсортированных по количеству лайков или году выпуска.");
        return directorService.getSortedDirectorList(directorId, sortBy);
    }

    //GET /films/popular?count={limit}&genreId={genreId}&year={year}
    //Возвращает список самых популярных фильмов указанного жанра за нужный год.
    //GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано, верните первые 10.
    //Объединил старый метод и необходимую новую реализацию методов
    @GetMapping("/popular")
    public List<Film> getPopularWithYearForYear(@RequestParam(required = false, defaultValue = "10") int count,
                                                @RequestParam(required = false) Long genreId,
                                                @RequestParam(required = false) Integer year,
                                                @RequestParam(required = false, defaultValue = "10") int limit) {

        if (genreId == null && year == null) {
            // Обработка запроса /films/popular?count={count}
            log.info("Получили запрос на получение списка самых залайканых фильмов количеством: {} шт.", count);
            return filmService.getPopularFilms(count);
        }

        if (year == null) {
            // Обработка случая, когда параметр year не указан в запросе
            log.info("Получили запрос на получение списка" +
                    " самых популярных фильмов указанного жанра с айди = {}", genreId);
            return filmService.getPopularWithGenre(limit, genreId);
        }

        if (genreId == null) {
            // Обработка случая, когда параметр genreId не указан в запросе
            log.info("Получили запрос на получение списка" +
                    " самых популярных фильмов за нужный год = {}.", year);
            return filmService.getPopularWithYear(limit, year);
        }

        log.info("Получили запрос на получение списка размером = {}," +
                " самых популярных фильмов указанного жанра с айди = {} за нужный год = {}.", limit, genreId, year);
        // Обработка случая, когда указаны все параметры
        return filmService.getPopularWithYearForYear(limit, genreId, year);
    }

    @GetMapping("/search") //после поиска пользователю выдается список фильмов по популярности
    public List<Film> searchFilmsByQuery2(@RequestParam String query, @RequestParam String by) {
        log.info("Вызван метод getFilmsByQuery - поиск фильмов по названию и/или режиссеру" +
                " с query " + query + " с by " + by);
        return filmService.searchFilmsByQuery(query, by);
    }
}
