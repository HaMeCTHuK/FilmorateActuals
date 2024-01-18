package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindUserById() {
        // Подготавливаем данные для теста
        User newUser = new User(
                "user@email.ru",
                "vanya123",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.create(newUser);

        // вызываем тестируемый метод
        User savedUser = userStorage.get(newUser.getId());

        // проверяем утверждения
        assertThat(savedUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newUser);        // и сохраненного пользователя - совпадают
    }

    @Test
    void testUpdateUser() {
        // Подготавливаем данные для теста
        User newUser = new User(
                "user@email.ru",
                "vanya123",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        User savedUser = userStorage.create(newUser);

        // Меняем некоторые данные
        savedUser.setEmail("newemail@email.com");
        savedUser.setLogin("newlogin");
        savedUser.setName("New Name");
        savedUser.setBirthday(LocalDate.now());

        // Вызываем тестируемый метод
        User updatedUser = userStorage.update(savedUser);

        // Проверяем утверждения
        assertThat(updatedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(savedUser);
    }

    @Test
    void testGetAllUsers() {
        // Подготавливаем данные для теста
        User user1 = new User(
                "user1@email.ru",
                "user1",
                "User One",
                LocalDate.of(1990, 1, 1));
        User user2 = new User(
                "user2@email.ru",
                "user2",
                "User Two",
                LocalDate.of(1995, 5, 5));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.create(user1);
        userStorage.create(user2);

        // Вызываем тестируемый метод
        List<User> userList = userStorage.getAll();

        // Проверяем утверждения
        Assertions.assertThat(userList)
                .isNotNull()
                .hasSize(2)
                .contains(user1, user2);
    }

    @Test
    void testDeleteUser() {
        // Подготавливаем данные для теста
        User newUser = new User("user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        User savedUser = userStorage.create(newUser);

        // Вызываем тестируемый метод
        userStorage.delete(savedUser.getId());

        // Проверяем, что пользователь больше не существует
        assertThrows(DataNotFoundException.class, () -> userStorage.get(savedUser.getId()));
    }
}