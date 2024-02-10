package ru.java.practicum.filmorate.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.model.Event;
import ru.java.practicum.filmorate.storage.EventsStorage;

import java.util.List;

// регистрация событий добавление друга пользователя
@Slf4j
@Component
public class FriendEvents implements Events {
    private final EventsStorage eventsStorage;

    @Autowired
    public FriendEvents(EventsStorage eventsStorage) {
        this.eventsStorage = eventsStorage;
    }

    @Override
    public void add(Long userId, Long entityId) {
        log.info("Событие добаление друга {} пользователем {} добавлено в БД", userId, entityId);
        eventsStorage.addEvent(userId, entityId, EventType.FRIEND, EventOperation.ADD);
    }

    @Override
    public void remove(Long userId, Long entityId) {
        log.info("Событие удаление друга {} пользователем {} добавлено в БД", userId, entityId);
        eventsStorage.addEvent(userId, entityId, EventType.FRIEND, EventOperation.REMOVE);
    }

    @Override
    public void update(Long userId, Long entityId) {

    }

    @Override
    public List<Event> getFeed(Long userId) {
        return eventsStorage.getUserEvents(userId);
    }
}
