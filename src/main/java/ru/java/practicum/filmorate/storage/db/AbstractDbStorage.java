package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.java.practicum.filmorate.model.BaseUnit;
import ru.java.practicum.filmorate.storage.AbstractStorage;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class AbstractDbStorage<T extends BaseUnit> implements AbstractStorage<T> {

    private JdbcTemplate jdbcTemplate;

    @Override
    public T create(T data) {
        String sql = getCreateSql();
        jdbcTemplate.update(sql, getParameters(data));
        log.info("Добавлен объект: " + data);
        return data;
    }

    @Override
    public T update(T data) {
        String sql = getUpdateSql();
        jdbcTemplate.update(sql, getParameters(data));
        log.info("Обновлен объект: " + data);
        return data;
    }

    @Override
    public List<T> getAll() {
        String sql = "SELECT * FROM " + getTableName();
        return jdbcTemplate.query(sql, getRowMapper());
    }

    @Override
    public T get(Long id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, getRowMapper(), id);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Удален объект с id=" + id);
    }

    protected abstract String getTableName();

    protected abstract String getCreateSql();

    protected abstract String getUpdateSql();

    protected abstract Object[] getParameters(T data);

    protected abstract RowMapper<T> getRowMapper();
}
