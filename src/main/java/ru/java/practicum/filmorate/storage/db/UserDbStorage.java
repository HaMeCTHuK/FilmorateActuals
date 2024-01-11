package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDbStorage extends AbstractDbStorage<User> implements UserStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAllFriends(Long userId) {
        return null;
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {
        return false;
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        return false;
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

    static User createUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate()) ///проверить
                .

    }
}
