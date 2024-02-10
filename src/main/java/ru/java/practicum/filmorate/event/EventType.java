package ru.java.practicum.filmorate.event;

// типы событий
public enum EventType {
    LIKE,
    REVIEW,
    FRIEND
    ;

    @Override
    public String toString() {
        switch (this.ordinal()) {
            case 0:
                return "LIKE";
            case 1:
                return "REVIEW";
            case 2:
                return "FRIEND";
            default:
                return null;
        }
    }

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
