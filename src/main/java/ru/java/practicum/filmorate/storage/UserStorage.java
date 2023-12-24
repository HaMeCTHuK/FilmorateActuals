package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage extends AbstractStorage<User> {

    List<User> getAllFriends(Long userId);

    User addFriend(Long userId, Long friendId);

    User deleteFriend(Long userId, Long friendId);
}
