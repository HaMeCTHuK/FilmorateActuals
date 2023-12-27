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
        log.info("Список друзей получен {} ", user.getClassSet().size());
        log.info("Список друзей: " + user.getClassSet());
        ArrayList<User> allFriends = new ArrayList<>();
        for (Long friendId : user.getClassSet()) {
            allFriends.add(get(friendId));
        }
        return allFriends;
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {
        User user = get(userId);
        User friend = get(friendId);
        boolean isAdded = (user.addFriend(friendId) && friend.addFriend(userId));
        log.info("Друг добавлен");
        log.info("Друг: " + get(friendId));
        return isAdded;

    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        User user = get(userId);
        User friend = get(friendId);
        boolean isDeleted = (user.deleteFriend(friendId) && friend.deleteFriend(userId));
        log.info("Друг удален");
        return isDeleted;

    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        User user = get(userId);
        User friend = get(friendId);

        List<User> commonFriendsList = new ArrayList<>();
        for (Long commonFriendId : user.getClassSet()) {
            if (friend.getClassSet().contains(commonFriendId)) {
                commonFriendsList.add(get(commonFriendId));
            }
        }
        log.info("Получен список общих друзей: " + commonFriendsList.size());
        log.info("Список общих друзей: " + commonFriendsList);
        return commonFriendsList;

    }
}
