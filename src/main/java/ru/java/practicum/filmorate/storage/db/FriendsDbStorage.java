package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.storage.FriendsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendsDbStorage implements FriendsStorage {

    private final JdbcTemplate jdbcTemplate;

    // Метод для получения списка всех друзей пользователя
    @Override
    public List<User> getAllFriends(Long userId) {
        String sql = "SELECT F.friend_id, U.* " +
                "FROM FRIENDS F " +
                "JOIN USERS U ON F.friend_id = U.id " +
                "WHERE F.user_id = ?";

        try {
            return jdbcTemplate.query(sql, FriendsDbStorage::createUser, userId);
        } catch (DataNotFoundException e) {
            // Если друзей нет, возвращаем пустой список
            return Collections.emptyList();
        }
    }

    // Метод для добавления друга пользователю
    @Override
    public boolean addFriend(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO FRIENDS (user_id, friend_id) VALUES (?, ?)";
        int affectedRows = jdbcTemplate.update(sqlQuery, userId, friendId);

        // Проверка наличия взаимной дружбы
        checkAndSetFriendship(userId, friendId);

        return affectedRows > 0;
    }

    // Метод для удаления друга у пользователя
    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE user_id = ? AND friend_id = ?";
        int affectedRows = jdbcTemplate.update(sqlQuery, userId, friendId);

        jdbcTemplate.update("UPDATE FRIENDS" +
                " SET friendship = 'unconfirmed' WHERE user_id = ? AND friend_id = ?", friendId, userId);

        return affectedRows > 0;
    }

    // Метод для получения общих друзей у двух пользователей
    @Override
    public List<User> getCommonFriends(Long userId, Long friendId) {
        String sqlQuery = "SELECT u.* FROM USERS u " +
                "JOIN FRIENDS f1 ON u.id = f1.friend_id " +
                "JOIN FRIENDS f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";

        return jdbcTemplate.query(sqlQuery, FriendsDbStorage::createUser, userId, friendId);
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

    // Вспомогательный метод для проверки взаимной дружбы
    private boolean checkAndSetFriendship(Long userId, Long friendId) {
        String sqlQuery = "SELECT " +
                "SUM(CASE WHEN user_id = ? AND friend_id = ? OR user_id = ? AND friend_id = ? THEN 1 ELSE 0 END) " +
                "FROM FRIENDS";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, userId, friendId, friendId, userId);

        if (count == null) {
            return false;
        }

        if (count == 2) {
            // Обновление статуса friendship на "confirmed"
            jdbcTemplate.update("UPDATE FRIENDS" +
                    " SET friendship = 'confirmed' WHERE user_id = ? AND friend_id = ?", userId, friendId);
            jdbcTemplate.update("UPDATE FRIENDS" +
                    " SET friendship = 'confirmed' WHERE user_id = ? AND friend_id = ?", friendId, userId);
        } else {
            // Вставка новой записи с friendship по умолчанию "unconfirmed"
            jdbcTemplate.update("UPDATE FRIENDS" +
                    " SET friendship = 'unconfirmed' WHERE user_id = ? AND friend_id = ?", userId, friendId);
        }

        return count == 2;
    }

    //Метод для получения статуса дружбы
    public String getFriendshipStatus(Long userId, Long friendId) {
        String sqlQuery = "SELECT friendship " +
                "FROM friends " +
                "WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";

        List<String> friendshipStatusList = jdbcTemplate.queryForList(
                sqlQuery,
                String.class,
                userId,
                friendId,
                friendId,
                userId);

            return friendshipStatusList.get(0);

    }
}
