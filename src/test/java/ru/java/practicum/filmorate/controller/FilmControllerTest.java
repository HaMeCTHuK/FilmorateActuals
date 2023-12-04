package ru.java.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.java.practicum.filmorate.exception.ValidationException;
import ru.java.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    FilmController filmController;

    @BeforeEach
    void setUp() {

        filmController = new FilmController();

    }

    @Test
    void dateFilmValidate() {

        Film film = Film.builder()
                .name("Vasyan")
                .description("Desc")
                .duration(200)
                .releaseDate(LocalDate.of(1791,11,12))
                .build();



        Assertions.assertThrows(ValidationException.class, () -> filmController.validate(film));
    }

    @Test
    void validateFilmOk() {

        Film film = Film.builder()
                .name("Vasyan")
                .description("Desc")
                .duration(200)
                .releaseDate(LocalDate.of(1991,11,12))
                .build();

        Assertions.assertDoesNotThrow(() -> filmController.validate(film));
    }

}