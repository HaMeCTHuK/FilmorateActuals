package ru.java.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.java.practicum.filmorate.model.Genre;
import ru.java.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<Genre> getAllGenre() {
        final List<Genre> mpaRating = genreService.getAllGenres();
        log.info("Получаем список рейтинга МРА");
        return mpaRating;
    }

    @GetMapping
    @RequestMapping("/{id}")
    public Genre getGenre(@PathVariable long id) {
        Genre genre = genreService.getGenre(id);
        log.info("Получаем рейтинг МРА по айди");
        return genre;
    }

}
