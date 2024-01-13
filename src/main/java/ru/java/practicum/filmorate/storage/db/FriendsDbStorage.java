package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.storage.FriendsStorage;

import java.sql.ResultSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendsDbStorage extends AbstractDbStorage<User> implements FriendsStorage {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<User> getAllFriends(Long userId) {
        String sqlQuery = "SELECT * FROM FRIENDS WHERE user_id = ?";
        return jdbcTemplate.query(sqlQuery, FriendsDbStorage::createUser, userId);
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO FRIENDS (user_id, friend_id) VALUES (?, ?)";
        int affectedRows = jdbcTemplate.update(sqlQuery, userId, friendId);
        return affectedRows > 0;
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE user_id = ? AND friend_id = ?";
        int affectedRows = jdbcTemplate.update(sqlQuery, userId, friendId);
        return affectedRows > 0;
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long friendId) {
        String sqlQuery = "SELECT u.* FROM USERS u " +
                "JOIN FRIENDS f1 ON u.id = f1.friend_id " +
                "JOIN FRIENDS f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";

        return jdbcTemplate.query(sqlQuery, FriendsDbStorage::createUser, userId, friendId);
    }

    @Override
    protected String getTableName() {
        return "FRIENDS";
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO FRIENDS (user_id, friend_id) VALUES (?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE FRIENDS SET user_id=?, friend_id=? WHERE user_id=? AND friend_id=?";
    }

    @Override
    protected Object[] getParameters(User data) {
        return new Object[]{data.getId(), data.getId()};
    }

    @Override
    protected RowMapper<User> getRowMapper() {
        return FriendsDbStorage::createUser;
    }

    static User createUser(ResultSet rs, int rowNum) {
        // Implement the creation logic
        return null;
    }
}
