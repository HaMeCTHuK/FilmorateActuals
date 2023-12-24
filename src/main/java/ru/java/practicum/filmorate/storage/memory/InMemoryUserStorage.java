package ru.java.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.model.User;
import ru.java.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage extends InMemoryBaseStorage<User> implements UserStorage {

    @Override
    public List<User> getAllFriends(Long userId) {
        User user = get(userId);
        log.info("Список друзей получен {} ", user.getFriends().size());
        log.info("Список друзей: " + user.getFriends());
        ArrayList<User> allFriends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            allFriends.add(get(friendId));
        }
        return allFriends;
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        User user = get(userId);
        User friend = get(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Друг добавлен");
        log.info("Друг: " + get(friendId));
        return user;

    }

    @Override
    public User deleteFriend(Long userId, Long friendId) {
        User user = get(userId);
        User friend = get(friendId);
        log.info("Друг удален");
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        return user;

    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        User user = get(userId);
        User friend = get(friendId);

        List<User> commonFriendsList = new ArrayList<>();
        for (Long commonFriendId : user.getFriends()) {
            if (friend.getFriends().contains(commonFriendId)) {
                commonFriendsList.add(get(commonFriendId));
            }
        }
        log.info("Получен список общих друзей: " + commonFriendsList.size());
        log.info("Список общих друзей: " + commonFriendsList);
        return commonFriendsList;

    }

}
