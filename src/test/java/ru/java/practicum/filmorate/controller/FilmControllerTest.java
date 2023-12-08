package ru.java.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.java.practicum.filmorate.exception.ValidationException;
import ru.java.practicum.filmorate.model.Film;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;



class FilmControllerTest {

    FilmController filmController;
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

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

    @Test
    public void testValidFilm() {
        Film film = Film.builder()
                .name("Marusya")
                .description("Description")
                .releaseDate(LocalDate.of(1991,11,12))
                .duration(120)
                .build();

        Assertions.assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    public void testInvalidFilmName() {
        Film film = Film.builder()
                .description("Description")
                .releaseDate(LocalDate.of(1991,11,12))
                .duration(120)
                .build();

        Assertions.assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    public void testInvalidFilmDescription() {
        Film film = Film.builder()
                .name("Film")
                .description("a".repeat(201)) // >200
                .releaseDate(LocalDate.of(1991,11,12))
                .duration(120)
                .build();

        Assertions.assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    public void testInvalidFilmReleaseDate() {
        Film film = Film.builder()
                .name("Film")
                .description("Description")
                .duration(120)
                .build();

        Assertions.assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    public void testInvalidFilmDuration() {
        Film film = Film.builder()
                .name("Film")
                .description("Description")
                .releaseDate(LocalDate.of(1991,11,12))
                .duration(-5)
                .build();

        Assertions.assertFalse(validator.validate(film).isEmpty());
    }

}