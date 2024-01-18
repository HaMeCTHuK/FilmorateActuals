package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.java.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendsDbStorageTest {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Test
    void getAllFriendsEmpty() {

        User newUser = new User(
                "user@email.ru",
                "vanya123",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));

        User newUser2 = new User(
                "user222222@email.ru",
                "Petruxa",
                "Boroda pivnaya",
                LocalDate.of(2004, 1, 1));

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        FriendsDbStorage friendsDbStorage = new FriendsDbStorage(jdbcTemplate);

        User createdUser = userStorage.create(newUser);
        User createdUser2 = userStorage.create(newUser2);

        friendsDbStorage.addFriend(createdUser.getId(), createdUser2.getId());
        friendsDbStorage.addFriend(createdUser2.getId(), createdUser.getId());


        List<User> friends = friendsDbStorage.getAllFriends(createdUser.getId());

        assertNotNull(friends);
        assertFalse(friends.isEmpty());
    }

    @Test
    void checkCommonFriend() {

        User newUser = new User(
                "user@email.ru",
                "vanya123",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));

        User newUser2 = new User(
                "user222222@email.ru",
                "Petruxa",
                "Boroda pivnaya",
                LocalDate.of(2004, 1, 1));

        User newUser3 = new User(
                "user33333333@email.ru",
                "tras",
                "pivo vodka",
                LocalDate.of(2014, 2, 4));

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        FriendsDbStorage friendsDbStorage = new FriendsDbStorage(jdbcTemplate);

        User createdUser = userStorage.create(newUser);
        User createdUser2 = userStorage.create(newUser2);
        User createdUser3 = userStorage.create(newUser3);

        friendsDbStorage.addFriend(createdUser.getId(), createdUser3.getId());
        friendsDbStorage.addFriend(createdUser2.getId(), createdUser3.getId());


        // Проверяем общих друзей
        List<User> commonFriends = friendsDbStorage.getCommonFriends(createdUser.getId(), createdUser2.getId());
        assertNotNull(commonFriends);
        assertFalse(commonFriends.isEmpty());
    }

    @Test
    void deleteFriend() {
        User newUser = new User(
                "user@email.ru",
                "vanya123",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));

        User newUser2 = new User(
                "user222222@email.ru",
                "Petruxa",
                "Boroda pivnaya",
                LocalDate.of(2004, 1, 1));

        User newUser3 = new User(
                "user33333333@email.ru",
                "tras",
                "pivo vodka",
                LocalDate.of(2014, 2, 4));

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        FriendsDbStorage friendsDbStorage = new FriendsDbStorage(jdbcTemplate);

        User createdUser = userStorage.create(newUser);
        User createdUser2 = userStorage.create(newUser2);
        User createdUser3 = userStorage.create(newUser3);

        friendsDbStorage.addFriend(createdUser.getId(), createdUser3.getId());
        friendsDbStorage.addFriend(createdUser2.getId(), createdUser3.getId());

        assertTrue(friendsDbStorage.deleteFriend(createdUser.getId(), createdUser3.getId()));

        // Проверка отсутствия общих друзей
        List<User> commonFriends = friendsDbStorage.getCommonFriends(createdUser.getId(), createdUser3.getId());
        assertNotNull(commonFriends);
        assertTrue(commonFriends.isEmpty());
    }

    @Test
    void getCommonFriends() {

        FriendsDbStorage friendsDbStorage = new FriendsDbStorage(jdbcTemplate);

        Long userId = 1L;  // Замените на существующий ID пользователя
        Long friendId = 2L;  // Замените на существующий ID друга

        List<User> commonFriends = friendsDbStorage.getCommonFriends(userId, friendId);

        assertNotNull(commonFriends);
        assertFalse(commonFriends.isEmpty());
    }

    @Test
    void areFriendsConfirmed() {
        User newUser = new User(
                "user@email.ru",
                "vanya123",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));

        User newUser2 = new User(
                "user222222@email.ru",
                "Petruxa",
                "Boroda pivnaya",
                LocalDate.of(2004, 1, 1));

        User newUser3 = new User(
                "user33333333@email.ru",
                "tras",
                "pivo vodka",
                LocalDate.of(2014, 2, 4));

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        FriendsDbStorage friendsDbStorage = new FriendsDbStorage(jdbcTemplate);

        User createdUser = userStorage.create(newUser);
        User createdUser2 = userStorage.create(newUser2);
        User createdUser3 = userStorage.create(newUser3);


        friendsDbStorage.addFriend(createdUser.getId(), createdUser2.getId());
        friendsDbStorage.addFriend(createdUser2.getId(), createdUser.getId());
        friendsDbStorage.addFriend(createdUser3.getId(), createdUser.getId());

        String statusConfirmed = friendsDbStorage.getFriendshipStatus(createdUser.getId(), createdUser2.getId());
        String statusUnconfirmed = friendsDbStorage.getFriendshipStatus(createdUser3.getId(), createdUser.getId());


        assertEquals("confirmed", statusConfirmed);

        assertEquals("unconfirmed", statusUnconfirmed);
    }
}
