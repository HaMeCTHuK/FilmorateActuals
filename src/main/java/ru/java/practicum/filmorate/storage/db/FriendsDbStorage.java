package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.storage.FriendsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendsDbStorage implements FriendsStorage {

    private final JdbcTemplate jdbcTemplate;

    // Метод для получения списка всех друзей пользователя
    @Override
    public List<User> getAllFriends(Long userId) {
        String sqlQuery = "SELECT * FROM FRIENDS WHERE user_id = ?";
        return jdbcTemplate.query(sqlQuery, FriendsDbStorage::createUser, userId);
    }

    // Метод для добавления друга пользователю
    @Override
    public boolean addFriend(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO FRIENDS (user_id, friend_id) VALUES (?, ?)";
        int affectedRows = jdbcTemplate.update(sqlQuery, userId, friendId);
        return affectedRows > 0;
    }

    // Метод для удаления друга у пользователя
    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE user_id = ? AND friend_id = ?";
        int affectedRows = jdbcTemplate.update(sqlQuery, userId, friendId);
        return affectedRows > 0;
    }

    // Метод для получения общих друзей у двух пользователей
    public List<User> getCommonFriends(Long userId, Long friendId) {
        String sqlQuery = "SELECT u.* FROM USERS u " +
                "JOIN FRIENDS f1 ON u.id = f1.friend_id " +
                "JOIN FRIENDS f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";

        return jdbcTemplate.query(sqlQuery, FriendsDbStorage::createUser, userId, friendId);
    }

    // Вспомогательный метод для извлечения параметров пользователя из ResultSet
    protected Object[] getParameters(User user) {
        return new Object[]{user.getId(), user.getId()};
    }

    // Вспомогательный метод для создания объекта User из ResultSet
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
