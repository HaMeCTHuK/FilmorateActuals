package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.exception.DataNotFoundException;
import ru.java.practicum.filmorate.model.Mpa;
import ru.java.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaDbStorage extends AbstractDbStorage<Mpa> implements MpaStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAll() {
        String sqlQuery = "SELECT * FROM MPARating";
        return jdbcTemplate.query(sqlQuery, MpaDbStorage::createMpa);

    }

    @Override
    public Mpa get(Long id) {
        String sqlQuery = "SELECT * FROM MPARating WHERE id = ?";
        List<Mpa> genres = jdbcTemplate.query(sqlQuery, MpaDbStorage::createMpa, id);
        if (genres.size() != 1) {
            throw new DataNotFoundException("При получении MPA по id список не равен 1");
        }
        return genres.get(0);
    }

    @Override
    protected String getTableName() {
        return "MPARating";
    }

    @Override
    protected String getCreateSql() {
        return "INSERT INTO MPARating (id, rating_name) VALUES (?, ?)";
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE MPARating SET rating_name = ? WHERE id = ?";
    }

    @Override
    protected Object[] getParameters(Mpa data) {
        return new Object[]{data.getId(), data.getRatingName()};
    }

    @Override
    protected RowMapper<Mpa> getRowMapper() {
        return MpaDbStorage::createMpa;
    }

    static Mpa createMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("id"))
                .ratingName(rs.getString("rating_name"))
                .build();
    }
}
