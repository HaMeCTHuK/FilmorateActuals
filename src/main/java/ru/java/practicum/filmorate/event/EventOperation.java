package ru.java.practicum.filmorate.event;

// типы операций событий
public enum EventOperation {
    ADD,
    REMOVE,
    UPDATE
    ;

    @Override
    public String toString() {
        switch (this.ordinal()) {
            case 0:
                return "ADD";
            case 1:
                return "REMOVE";
            case 2:
                return "UPDATE";
            default:
                return null;
        }
    }

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