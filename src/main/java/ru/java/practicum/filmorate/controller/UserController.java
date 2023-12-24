package ru.java.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseBody
    public List<User> getAllUsers() {
        log.info("Текущее количество пользователей: {}", userService.getAll().size());
        return userService.getAll();
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        log.info("Пытаемся добавить пользователя: {}", user);
        return userService.create(user);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        log.info("Пытаемся обновить пользователя : {}", user);
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User getUser(@RequestBody @PathVariable Long id) {
        log.info("Получаем объект по id: {}", id);
        return userService.getData(id);
    }

    // PUT /users/{id}/friends/{friendId} — добавление в друзья.
    @PutMapping("/{id}/friends/{friendId}")
    public User addFriends(@RequestBody @PositiveOrZero @PathVariable Long id, @PathVariable Long friendId) {
        log.info("Добавляем пользователю ID: " + id + ", друга с friendId: " + friendId);
        return userService.addFriend(id, friendId);
    }

    // DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@RequestBody @PositiveOrZero @PathVariable Long id, @PathVariable Long friendId) {
        log.info("Удаляем у пользователя ID: " + id + " друга с friendId: " + friendId);
        return userService.deleteFriend(id, friendId);
    }

    //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.

    @GetMapping("/{id}/friends")
    public List<User> getFriendsList(@RequestBody @PathVariable Long id) {
        log.info("Получаем список друзей пользователя ID: " + id);
        return userService.getAllFriends(id);
    }

    //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriend(@RequestBody @PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получаем список общих друзей пользоватеей ID: " + id + " и " + otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
