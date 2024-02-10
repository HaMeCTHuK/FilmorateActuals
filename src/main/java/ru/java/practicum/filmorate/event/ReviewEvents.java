package ru.java.practicum.filmorate.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.model.Event;
import ru.java.practicum.filmorate.storage.EventsStorage;

import java.util.List;

// регистрация событий ревью пользователя
@Slf4j
@Component
public class ReviewEvents implements Events {
    EventsStorage eventsStorage;

    @Autowired
    public ReviewEvents(EventsStorage eventsStorage) {
        this.eventsStorage = eventsStorage;
    }

    @Override
    public void add(Long userId, Long entityId) {
        log.info("Событие добаление ревью {} пользователем {} добавлено в БД", userId, entityId);
        eventsStorage.addEvent(userId, entityId, EventType.REVIEW, EventOperation.ADD);
    }

    @Override
    public void remove(Long userId, Long entityId) {
        log.info("Событие удаление ревью {} пользователем {} добавлено в БД", userId, entityId);
        eventsStorage.addEvent(userId, entityId, EventType.REVIEW, EventOperation.REMOVE);
    }

    @Override
    public void update(Long userId, Long entityId) {
        log.info("Событие обновления ревью {} пользователем {} добавлено в БД", userId, entityId);
        eventsStorage.addEvent(userId, entityId, EventType.REVIEW, EventOperation.UPDATE);
    }

    @Override
    public List<Event> getFeed(Long userId) {
        return eventsStorage.getUserEvents(userId);
    }
}
