package ru.java.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.java.practicum.filmorate.model.User;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends BaseController<User> {

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Текущее количество постов: {}", super.getData().size());
        return super.getData();
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {



        log.info("Пытаемся добавить пользователя: {}", user);
        return super.create(user);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {

        log.info("Пытаемся обновить пользователя : {}", user);
        return super.update(user);

    }

    @Override
    public void validate(User user) {

        if (user.getName() == null || user.getName().isBlank()) {
            log.info("имя для отображения пустое — используем использован логин : {}", user.getLogin());
            user.setName(user.getLogin());

        }
    }
}
