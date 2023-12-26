package ru.java.practicum.filmorate.storage.memory;


import lombok.extern.slf4j.Slf4j;
import ru.java.practicum.filmorate.model.BaseUnit;
import ru.java.practicum.filmorate.storage.AbstractStorage;


import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class InMemoryBaseStorage<T extends BaseUnit> implements AbstractStorage<T> {

    private final Map<Long, T> storage = new HashMap<>();

    private long generateId = 0;


    @Override
    public T create(T data) {
        data.setId(++generateId);
        storage.put(data.getId(), data);
        log.info("добавили " + data);
        return data;
    }

    @Override
    public T update(T data) {
        storage.put(data.getId(), data);
        log.info("обновили " + data);
        return data;
    }

    @Override
    public HashMap<Long, T> getAll() {
        return new HashMap<>(storage);
    }

    @Override
    public T get(long id) {
        return storage.get(id);
    }

}
