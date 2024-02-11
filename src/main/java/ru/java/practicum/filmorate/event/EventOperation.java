package ru.java.practicum.filmorate.event;

// типы операций событий
public enum EventOperation {
    ADD,
    REMOVE,
    UPDATE
    ;

    public static EventOperation fromString(String str) {
        switch (str) {
            case "ADD":
                return EventOperation.ADD;
            case "REMOVE":
                return EventOperation.REMOVE;
            case "UPDATE":
                return EventOperation.UPDATE;
            default:
                return null;
        }
    }
}