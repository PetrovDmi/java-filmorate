package ru.yandex.practicum.filmorate.storage.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.CustomException.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film getFilm(int filmId) {

        String sqlFilm = "SELECT f.filmId, f.name AS fname,f.description,f.releaseDate,f.duration,m.mpaId,m.name AS mname " +
                "FROM Film as f " +
                "INNER JOIN Mpa as m ON f.mpa = m.mpaId WHERE f.filmId = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlFilm, (rs, rowNum) -> makeFilm(rs), filmId);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Фильм с идентификатором " +
                    filmId + " не зарегистрирован!");
        }
        assert film != null;
        log.info("Найден фильм: {} {}", film.getId(), film.getName());
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT f.filmId, f.name AS fname,f.description,f.releaseDate,f.duration,m.mpaId,m.name AS mname " +
                "FROM Film as f " +
                "INNER JOIN Mpa as m ON f.mpa = m.mpaId";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> makeFilm(resultSet));
    }

    public List<Film> getMostPopularFilms() {
        String sqlPopularFilms =
                "SELECT f.filmId, f.name AS fname,f.description,f.releaseDate,f.duration,m.mpaId,m.name AS mname," +
                        "COUNT(l.userId) as likes " +
                        "FROM Film as f INNER JOIN Mpa as m ON f.mpa = m.mpaId " +
                        "INNER JOIN Likes As l ON l.filmId = f.filmId GROUP BY f.filmId ORDER BY likes DESC LIMIT 10";
        return jdbcTemplate.query(sqlPopularFilms, (resultSet, rowNum) -> makeFilm(resultSet));
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "insert into Film " +
                "(filmId, name, description, releaseDate, duration, mpa) " +
                "values (?, ?, ?, ?, ?, ?)";
        String sqlQueryGenre = "insert into filmGenres (filmId,genreId) VALUES (?,?)";
        String sqlQueryLike = "insert into Likes (filmId,userId) values (?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, film.getId());
            preparedStatement.setString(2, film.getName());
            preparedStatement.setString(3, film.getDescription());
            preparedStatement.setDate(4, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(5, film.getDuration());
            preparedStatement.setInt(6, film.getMpa().getId());
            return preparedStatement;
        }, keyHolder);

        for (FilmGenre genre : film.getGenres()) {
            jdbcTemplate.update(sqlQueryGenre, film.getId(), genre.getId());
        }

        for (Integer like : film.getLikes()) {
            jdbcTemplate.update(sqlQueryLike, film.getId(), like);
        }

        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        return getFilm(id);
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "Update Film Set name = ?,description = ?,releaseDate = ?,"
                + "duration = ?,mpa = ? Where filmId = ?";
        String sqlQueryGenre = "INSERT INTO filmGenres (filmId,genreId) VALUES (?,?)";
        String sqlQueryLike = "INSERT INTO Likes (filmId,userId)  VALUES (?,?)";
        String sqlQueryDeleteGenre = "DELETE FROM filmGenres WHERE filmId = ?";
        String sqlQueryDeleteLikes = "DELETE FROM Likes WHERE filmId = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setInt(5, film.getMpa().getId());
            preparedStatement.setInt(6, film.getId());
            return preparedStatement;
        });
        jdbcTemplate.update(sqlQueryDeleteLikes, film.getId());
        jdbcTemplate.update(sqlQueryDeleteGenre, film.getId());

        for (FilmGenre genre : film.getGenres()) {
            jdbcTemplate.update(sqlQueryGenre, film.getId(), genre.getId());
        }

        for (Integer like : film.getLikes()) {
            jdbcTemplate.update(sqlQueryLike, film.getId(), like);
        }

        return film;
    }

    @Override
    public void deleteFilm(Film film) {
        String sqlQuery = "delete from Film where FILMID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "select * from Likes where userId = ? and filmId = ?";
        SqlRowSet existLike = jdbcTemplate.queryForRowSet(sql, userId, filmId);
        if (!existLike.next()) {
            String setLike = "insert into Likes (userId, filmId) values (?, ?) ";
            jdbcTemplate.update(setLike, userId, filmId);
        }
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, userId, filmId);
        log.info(String.valueOf(sqlRowSet.next()));
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String deleteLike = "delete from Likes where filmId = ? and userId = ?";
        jdbcTemplate.update(deleteLike, filmId, userId);
    }

    private Film makeFilm(ResultSet resultSet) throws SQLException {
        int filmId = resultSet.getInt("FilmID");
        return new Film(
                filmId,
                resultSet.getString("fname"),
                resultSet.getString("Description"),
                Objects.requireNonNull(resultSet.getDate("ReleaseDate")).toLocalDate(),
                resultSet.getInt("Duration"),
                new Mpa(resultSet.getInt("mpaId"),
                        resultSet.getString("mname")),
                getFilmLikes(filmId),
                getFilmGenres(filmId));
    }

    private HashSet<FilmGenre> getFilmGenres(int id) {
        String sqlQuery = "SELECT * FROM Genre WHERE genreId IN "
                + "(SELECT genreId FROM FilmGenres WHERE filmId = ?)";
        return new HashSet<>(jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> new FilmGenre(rs.getInt("genreId"),
                        rs.getString("genreName")), id)) {
        };
    }

    private HashSet<Integer> getFilmLikes(int id) {
        String sqlQuery = "SELECT userId FROM Likes WHERE filmId = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> rs.getInt("userId"), id));
    }

    private String getMpaName(int mpaId) {
        String sqlMpaName = "Select name From Mpa Where mpaId = ?";
        return jdbcTemplate.queryForObject(sqlMpaName, String.class, mpaId);
    }
}
