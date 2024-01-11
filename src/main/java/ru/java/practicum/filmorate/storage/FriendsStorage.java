package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage extends AbstractStorage<Film> {

    List<User> getAllFriends(Long userId);

    boolean addFriend(Long userId, Long friendId);

    boolean deleteFriend(Long userId, Long friendId);

    List<User> getCommonFriends(Long userId, Long friendId);
}
