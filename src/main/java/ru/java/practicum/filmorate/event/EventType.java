package ru.java.practicum.filmorate.event;

// типы событий
public enum EventType {
    LIKE,
    REVIEW,
    FRIEND
    ;

    public static EventType fromString(String str) {
        switch (str) {
            case "LIKE":
                return EventType.LIKE;
            case "REVIEW":
                return EventType.REVIEW;
            case "FRIEND":
                return EventType.FRIEND;
            default:
                return null;
        }
    }
}
