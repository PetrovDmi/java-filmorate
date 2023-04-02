package ru.yandex.practicum.filmorate.storage.DAO;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.CustomException.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaById(int mpaId) {
        String sqlMpa = "select * from Mpa where mpaId = ?";
        Mpa mpa;
        try {
            mpa = jdbcTemplate.queryForObject(sqlMpa, (rs, rowNum) -> makeMpa(rs), mpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Возрастной рейтинг с идентификатором " +
                    mpaId + " не зарегистрирован!");
        }
        return mpa;
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = "select * from Mpa";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> makeMpa(resultSet));
    }

    private Mpa makeMpa(ResultSet resultSet) throws SQLException {
        int mpaId = resultSet.getInt("mpaId");
        return new Mpa(mpaId, resultSet.getString("name"));
    }
}
