package ru.java.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.exception.IncorrectParameterException;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.storage.FriendsStorage;
import ru.java.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService extends AbstractService<User> {

    private final FriendsStorage friendsStorage;

    @Autowired
    public UserService(@Qualifier( "userDbStorage") UserStorage userStorage, FriendsStorage friendsStorage) {
        this.abstractStorage = userStorage;
        this.friendsStorage = friendsStorage;
    }

    @Override
    public void validate(User user) {
        log.info("User id = {}", user.getId());
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("имя для отображения пустое — используем использован логин : {}", user.getLogin());
            user.setName(user.getLogin());
        }

    }

    @Override
    public void validateParameter(Long userId) {
        if (userId == null) {
            throw new IncorrectParameterException("Некорректные параметры поля, проверь null");
        }
        if (getData(userId) == null) {
            throw new DataNotFoundException("Такого пользователя с айди нет" + userId);
        }

    }

    @Override
    public void validateParameters(Long userId, Long friendId) {
        User user = getData(userId);
        User friend = getData(friendId);
        log.info("Валидация параметров UserService");
        if (user == null || friend == null) {
            throw new DataNotFoundException("Друг не добавлен, таких пользователей нет");
        }

    }

    public List<User> getAllFriends(Long userId) {
        validateParameter(userId);
        log.info("Получаем список друзей");
        return friendsStorage.getAllFriends(userId);
    }

    public boolean addFriend(Long userId, Long friendId) {
        validateParameters(userId, friendId);
        log.info("Добавляем пользователю ID: " + userId + ", друга с friendId: " + friendId);
        return friendsStorage.addFriend(userId, friendId);
    }

    public boolean deleteFriend(Long userId, Long friendId) {
        validateParameters(userId, friendId);
        log.info("Удаляем у пользователя ID: " + userId + " друга с friendId: " + friendId);
        return friendsStorage.deleteFriend(userId, friendId);
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        validateParameters(userId, friendId);
        log.info("Получаем список общих друзей пользоватеей ID: " + userId + " и " + friendId);
        return friendsStorage.getCommonFriends(userId, friendId);
    }

}
