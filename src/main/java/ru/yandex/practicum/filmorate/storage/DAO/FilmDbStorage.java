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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getGenre(int id) {
        String sqlGenre = "select * " +
                "from Genre " +
                "where genreId = ?";
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sqlGenre, (rs, rowNum) -> makeGenre(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Жанр с идентификатором " +
                    id + " не зарегистрирован!");
        }
        log.info("Найден фильм: {} {}", genre.getId(), genre.getName());
        return genre;
    }

    public Mpa getMpa(int id) {
        String sqlGenre = "select * " +
                "from Mpa " +
                "where mpaId = ?";
        Mpa mpa;
        try {
            mpa = jdbcTemplate.queryForObject(sqlGenre, (rs, rowNum) -> makeMpa(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Жанр с идентификатором " +
                    id + " не зарегистрирован!");
        }
        log.info("Найден фильм: {} {}", mpa.getId(), mpa.getName());
        return mpa;
    }

    private Genre makeGenre(ResultSet resultSet) throws SQLException {
        int genreId = resultSet.getInt("genreId");
        return new Genre(genreId, resultSet.getString("genreName"));
    }

    private Mpa makeMpa(ResultSet resultSet) throws SQLException {
        int mpaId = resultSet.getInt("mpaId");
        return new Mpa(mpaId, resultSet.getString("name"));
    }

    @Override
    public Film getFilm(int filmId) {

        String sqlFilm = "select * " +
                "from Film " +
                "where filmId = ?";
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
        String sql = "select * from Film";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> makeFilm(resultSet));
    }

    public Collection<Genre> getAllGenres() {
        String sql = "select * from Genre";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> makeGenre(resultSet));
    }

    public Collection<Mpa> getAllMpa() {
        String sql = "select * from Mpa";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> makeMpa(resultSet));
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "insert into Film " +
                "(name, description, releaseDate, duration, rate, mpaId) " +
                "values (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setLong(4, film.getDuration());
            preparedStatement.setInt(5, film.getRate());
            preparedStatement.setInt(6, Math.toIntExact(film.getMpa().getId()));
            return preparedStatement;
        }, keyHolder);

        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        return getFilm(id);
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
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

    @Override
    public Collection<Film> getMostPopularFilms(int count) {
        String sqlMostPopular = "select count(L.like) as likeRate" +
                ",Film.FILMID" +
                ",Film.name ,Film.description ,releaseDate ,duration ,rate ,R.ratingId, R.name, R.description from Film " +
                "left join Likes L on L.filmId = Film.filmId " +
                "inner join Mpa R on R.ratingId = Film.ratingId " +
                "group by Film.filmId " +
                "ORDER BY likeRate desc " +
                "limit ?";
        return jdbcTemplate.query(sqlMostPopular, (rs, rowNum) -> makeFilm(rs), count);
    }

    private Film makeFilm(ResultSet resultSet) throws SQLException {
        int filmId = resultSet.getInt("FilmID");
        return new Film(
                filmId,
                resultSet.getString("Name"),
                resultSet.getString("Description"),
                Objects.requireNonNull(resultSet.getDate("ReleaseDate")).toLocalDate(),
                resultSet.getLong("Duration"),
                resultSet.getInt("Rate"),
                new Mpa(resultSet.getInt("Mpa.mpaId"),
                        resultSet.getString("Mpa.name")));
    }

    private List<Integer> getFilmLikes(int filmId) {
        String sqlGetLikes = "select userId from Likes where filmId = ?";
        return jdbcTemplate.queryForList(sqlGetLikes, Integer.class, filmId);
    }
}
