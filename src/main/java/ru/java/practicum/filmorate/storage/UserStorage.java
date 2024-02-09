package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.model.User;

public interface UserStorage extends AbstractStorage<User> {
    User findUserById(long id); // add-reviews - дополнение в файл
}
