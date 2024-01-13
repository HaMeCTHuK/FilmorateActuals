package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbStorage extends AbstractDbStorage<User> implements UserStorage {

    private JdbcTemplate jdbcTemplate;

    //private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    /*    private static LocalDate parseLocalDate(String dateStr) {
        return LocalDate.parse(dateStr, dateFormatter);
    }*/

    @Override
    public List<User> getAllFriends(Long userId) {
        String sqlQuery = "SELECT * FROM FRIENDS WHERE user_id = ?";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::createUser, userId);
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
    public User create(User data) {
        return super.create(data);
    }

    @Override
    public User update(User data) {
        return super.update(data);
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT * FROM USERS";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::createUser);
    }

    @Override
    public User get(Long id) {
        return super.get(id);
    }

    @Override
    public void delete(Long id) {
        super.delete(id);
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        String sqlQuery = "SELECT u.* FROM USERS u " +
                "JOIN FRIENDS f1 ON u.id = f1.friend_id " +
                "JOIN FRIENDS f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";

        return jdbcTemplate.query(sqlQuery, UserDbStorage::createUser, userId, friendId);
    }

    @Override
    protected String getTableName() {
        return "USERS";
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO USERS (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE USERS SET email=?, login=?, name=?, birthday=? WHERE id=?";
    }

    @Override
    protected Object[] getParameters(User data) {
        return new Object[]{data.getEmail(), data.getLogin(), data.getName(), data.getBirthday(), data.getId()};
    }

    private static User mapUserFromResultSet(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    @Override
    protected RowMapper<User> getRowMapper() {
        return (rs, rowNum) -> mapUserFromResultSet(rs);
    }

    static User createUser(ResultSet rs, int rowNum) throws SQLException {
        return mapUserFromResultSet(rs);
    }
}
