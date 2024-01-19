package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Mpa;
import ru.java.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
   // Метод для создания нового MPA рейтинга в базе данных
    public Mpa create(Mpa mpa) {
        String sql = "INSERT INTO MPARating (id, rating_name) VALUES (?,?)";
        jdbcTemplate.update(sql,mpa.getId(), mpa.getName());
        return mpa;
    }

    // Метод для обновления информации о MPA рейтинге в базе данных
    @Override
    public Mpa update(Mpa mpa) {
        String sql = "UPDATE MPARating SET rating_name = ? WHERE id = ?";
        jdbcTemplate.update(sql, mpa.getName(), mpa.getId());
        return get(mpa.getId());
    }

    // Метод для удаления MPA рейтинга по его идентификатору
    @Override
    public void delete(Long id) {
        String sqlQuery = "DELETE FROM MPARating WHERE id = ?";
        int affectedRows = jdbcTemplate.update(sqlQuery, id);
        if (affectedRows != 1) {
            throw new DataNotFoundException("При удалении MPA по id количество удаленных строк не равно 1");
        }
    }

    // Метод для получения списка всех MPA рейтингов
    @Override
    public List<Mpa> getAll() {
        String sqlQuery = "SELECT * FROM MPARating";
        return jdbcTemplate.query(sqlQuery, MpaDbStorage::createMpa);
    }

    // Метод для получения информации о MPA рейтинге по его идентификатору
    @Override
    public Mpa get(Long id) {
        String sqlQuery = "SELECT * FROM MPARating WHERE id = ?";
        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, MpaDbStorage::createMpa, id);
        if (mpas.size() != 1) {
            throw new DataNotFoundException("При получении MPA по id список не равен 1");
        }
        return mpas.get(0);
    }

    // Вспомогательный метод для создания объекта MPA из ResultSet
    static Mpa createMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("rating_name"))
                .build();
    }
}
