package ru.java.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.exception.IncorrectParameterException;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.storage.memory.InMemoryUserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService extends AbstractService<User> {

    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {   //////
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public void validate(User user) {
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
        return inMemoryUserStorage.getAllFriends(userId);
    }

    public boolean addFriend(Long userId, Long friendId) {
        validateParameters(userId, friendId);
        log.info("Добавляем пользователю ID: " + userId + ", друга с friendId: " + friendId);
        return inMemoryUserStorage.addFriend(userId, friendId);
    }

    public boolean deleteFriend(Long userId, Long friendId) {
        validateParameters(userId, friendId);
        log.info("Удаляем у пользователя ID: " + userId + " друга с friendId: " + friendId);
        return inMemoryUserStorage.deleteFriend(userId, friendId);
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        validateParameters(userId, friendId);
        log.info("Получаем список общих друзей пользоватеей ID: " + userId + " и " + friendId);
        return inMemoryUserStorage.getCommonFriends(userId, friendId);
    }
}
