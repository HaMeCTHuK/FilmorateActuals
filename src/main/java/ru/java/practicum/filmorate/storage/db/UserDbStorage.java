package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

 /*   // Метод для получения списка всех друзей пользователя
    @Override
    public List<User> getAllFriends(Long userId) {
        String sqlQuery = "SELECT * FROM FRIENDS WHERE user_id = ?";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::createUser, userId);
    }

    // Метод для добавления друга пользователю
    @Override
    public boolean addFriend(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO FRIENDS (user_id, friend_id) VALUES (?, ?)";
        int affectedRows = jdbcTemplate.update(sqlQuery, userId, friendId);
        return affectedRows > 0;
    }

    // Метод для удаления друга пользователя
    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE user_id = ? AND friend_id = ?";
        int affectedRows = jdbcTemplate.update(sqlQuery, userId, friendId);
        return affectedRows > 0;
    }

     // Метод для получения списка общих друзей двух пользователей
    public List<User> getCommonFriends(Long userId, Long friendId) {
        String sqlQuery = "SELECT u.* FROM USERS u " +
                "JOIN FRIENDS f1 ON u.id = f1.friend_id " +
                "JOIN FRIENDS f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";

        return jdbcTemplate.query(sqlQuery, UserDbStorage::createUser, userId, friendId);
    }
    */

    // Метод для создания нового пользователя в базе данных
    @Override
    public User create(User user) {
        String sql = "INSERT INTO USERS (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, getParameters(user));
        log.info("Добавлен объект: " + user);
        return user;
    }

    // Метод для обновления информации о пользователе в базе данных
    @Override
    public User update(User user) {
        String sql = "UPDATE USERS SET email=?, login=?, name=?, birthday=? WHERE id=?";
        jdbcTemplate.update(sql, getParameters(user));
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
        return jdbcTemplate.queryForObject(sql, UserDbStorage::createUser, id);
    }

    // Метод для удаления пользователя по его идентификатору
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM USERS WHERE id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Удален объект с id=" + id);
    }

    // Вспомогательный метод для извлечения параметров пользователя из ResultSet
    protected Object[] getParameters(User user) {
        return new Object[]{user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId()};
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
}
