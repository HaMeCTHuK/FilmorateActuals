package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.event.EventOperation;
import ru.java.practicum.filmorate.event.EventType;
import ru.java.practicum.filmorate.model.Event;

import java.util.List;

// интерфейс для работы с сущностью Event в БД
public interface EventsStorage {
    void addEvent(Long userId, Long entityId, EventType eventType, EventOperation eventOperation);
    List<Event> getUserEvents(Long userId);
}
