package ru.yandex.practicum.filmorate.storage.DAO;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.CustomException.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void deleteFilmGenres(int filmId) {
        String deleteOldGenres = "delete from Genre where filmId = ?";
        jdbcTemplate.update(deleteOldGenres, filmId);
    }

    public void addFilmGenres(int filmId, Collection<Genre> genres) {
        String setNewGenres = "insert into Genre (filmId, genreId) values (?, ?) ON CONFLICT DO NOTHING";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Genre genre : genres) {
            Object[] rowData = new Object[] {filmId, genre.getId()};
            batchArgs.add(rowData);
        }
        jdbcTemplate.batchUpdate(setNewGenres, batchArgs);
    }

    @Override
    public Collection<Genre> getGenresByFilmId(int filmId) {
        String sqlGenre = "select Genre.genreId, genreName from Genre " +
                "INNER JOIN Genre GL on Genre.genreId = GL.genreId " +
                "where filmId = ?";
        return jdbcTemplate.query(sqlGenre, this::makeGenre, filmId);
    }

    @Override
    public Collection<Genre> getAllGenres() {
        String sqlGenre = "select genreId, genreName from Genre ORDER BY genreId";
        return jdbcTemplate.query(sqlGenre, this::makeGenre);
    }

    @Override
    public Genre getGenreById(int genreId) {
        String sqlGenre = "select * from Genre where genreId = ?";
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sqlGenre, this::makeGenre, genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Жанр с идентификатором " +
                    genreId + " не зарегистрирован!");
        }
        return genre;
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("genreID"), resultSet.getString("genreName"));
    }
}