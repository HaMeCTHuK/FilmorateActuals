package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.event.EventOperation;
import ru.java.practicum.filmorate.event.EventType;
import ru.java.practicum.filmorate.model.Event;
import ru.java.practicum.filmorate.storage.EventsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventDbStorage implements EventsStorage {
    private final JdbcTemplate jdbcTemplate;

    // добавление события в таблицу EVENTLOG
    @Override
    public void addEvent(Long userId, Long entityId, EventType eventType, EventOperation eventOperation) {
        String sql = "INSERT INTO EVENTLOG " +
                "(event_time, user_id, entity_id, event_type, operation) " +
                "values (?, ?, ?, ?, ?)";

        Long evenTypeId = getEventType(eventType);
        Long eventOperationId = getEventOperation(eventOperation);

        jdbcTemplate.update(sql,
                Timestamp.from(Instant.now()),
                userId,
                entityId,
                evenTypeId,
                eventOperationId
        );
    }

    // получение событий из таблицы EVENTLOG по ID пользователя
    @Override
    public List<Event> getUserEvents(Long userId) {
        String sql = "SELECT el.id \"id\", " +
                "el.event_time \"event_time\", " +
                "el.user_id \"user_id\", " +
                "et.name \"event_type\", " +
                "eo.name \"operation\", " +
                "el.entity_id \"entity_id\" " +
                "FROM EVENTLOG el " +
                "JOIN EVENTTYPES et on et.id = el.event_type " +
                "JOIN EVENTOP eo on eo.id = el.operation " +
                "WHERE el.user_id = ? ";

        return jdbcTemplate.query(sql, EventDbStorage::createEvent, userId);
    }

    // метод для создания объекта Event
    public static Event createEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .entityId(rs.getLong("entity_id"))
                .timestamp(rs.getTimestamp("event_time").getTime())
                .operation(EventOperation.fromString(rs.getString("operation")))
                .eventType(EventType.fromString(rs.getString("event_type")))
                .build();
    }

    // вспомогательный метод для получения типа события из таблицы EVENTTYPES
    private Long getEventType(EventType eventType) {
        String sql = "SELECT id " +
                "FROM EVENTTYPES " +
                "WHERE name = ?";

        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql, eventType.toString());
        resultSet.next();

        return resultSet.getLong("id");
    }

    // вспомогательный метод для получения операции по событию из таблицы EVENTOP
    private Long getEventOperation(EventOperation eventOperation) {
        String sql = "SELECT id " +
                "FROM EVENTOP " +
                "WHERE name = ?";

        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql, eventOperation.toString());
        resultSet.next();

        return resultSet.getLong("id");
    }
}
