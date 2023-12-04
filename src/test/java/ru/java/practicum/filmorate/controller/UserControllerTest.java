package ru.java.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.java.practicum.filmorate.model.User;
import java.time.LocalDate;


class UserControllerTest {

    UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void validateUserNameNull() {
        User user = User.builder()
                .email("www@mail.ru")
                .name(null)
                .login("Boroda")
                .birthday(LocalDate.of(1999,1,22))
                .build();

        userController.validate(user);

        Assertions.assertEquals("Boroda",user.getName());
    }

    @Test
    void validateUserNameBlank() {
        User user = User.builder()
                .email("www@mail.ru")
                .name("")
                .login("oda")
                .birthday(LocalDate.of(1999,1,22))
                .build();

        userController.validate(user);

        Assertions.assertEquals("oda",user.getName());
    }
}