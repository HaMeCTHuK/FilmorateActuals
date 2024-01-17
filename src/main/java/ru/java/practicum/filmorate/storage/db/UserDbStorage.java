package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.exception.IncorrectParameterException;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    // Метод для создания нового пользователя в базе данных
    @Override
    public User create(User user) {
        log.info("Отправляем данные для создания USER в таблице");
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("USERS")
                .usingGeneratedKeyColumns("id");

        Number id = simpleJdbcInsert.executeAndReturnKey(getParams(user));

        user.setId(id.intValue());
        log.info("Добавлен пользователь: {} {}", user.getId(), user.getEmail());
        return user;
    }

    // Метод для обновления информации о пользователе в базе данных
    @Override
    public User update(User user) {
        String sql = "UPDATE USERS SET email=?, login=?, name=?, birthday=? WHERE id=?";
        try {
            jdbcTemplate.update(sql, getParametersWithId(user));
        } catch (EmptyResultDataAccessException ex) {
            throw new DataNotFoundException("Данные о пользователе не найдены");
        }
        log.info("Обновлен объект: " + user);
        return user;
    }

    // Метод для получения списка всех пользователей
    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, UserDbStorage::createUser);
    }

    // Метод для получения информации о пользователе по его идентификатору
    @Override
    public User get(Long id) {
        String sql = "SELECT * FROM USERS WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, UserDbStorage::createUser, id);
        } catch (EmptyResultDataAccessException ex) {
            throw new DataNotFoundException("Данные о пользователе не найдены");
        }
    }

    // Метод для удаления пользователя по его идентификатору
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM USERS WHERE id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Удален объект с id= " + id);
    }

    // Вспомогательный метод для создания объекта пользователя из ResultSet
    private static User createUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    // Вспомогательный метод для извлечения параметров пользователя из ResultSet без id
    private static Map<String, String> getParams(User user) {

        Map<String, String> params = Map.of(
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", user.getBirthday().toString());
        return params;
    }

    // Вспомогательный метод для извлечения параметров пользователя из ResultSet c id
    protected Object[] getParametersWithId(User user) {
        return new Object[]{user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId()};
    }

}
