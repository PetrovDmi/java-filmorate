package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.CustomException.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.CustomException.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DAO.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.DAO.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.DAO.MpaDbStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static int increment = 1;
    private static final LocalDate MIN_REALIZE_DATE = LocalDate.of(1895, 12, 28);
    private final Validator validator;
    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final UserService userService;

    @Autowired
    public FilmService(Validator validator, @Qualifier("FilmDbStorage") FilmDbStorage filmStorage,
                       MpaDbStorage mpaDbStorage, GenreDbStorage genreDbStorage, @Autowired(required = false) UserService userService) {
        this.validator = validator;
        this.filmStorage = filmStorage;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.userService = userService;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getAllFilms();
    }

    public void add(Film film) {
        validate(film);
        filmStorage.addFilm(film);
    }

    public void update(Film film) {
        checkExistence(film);
        validate(film);
        filmStorage.updateFilm(film);
    }

    public void addLike(final String id, final String userId) {
        Film film = getStoredFilm(id);
        User user = userService.getUser(userId);
        filmStorage.addLike(film.getId(), user.getId());
    }

    public void deleteLike(final String id, final String userId) {
        Film film = getStoredFilm(id);
        User user = userService.getUser(userId);
        filmStorage.deleteLike(film.getId(), user.getId());
    }

    public List<Film> getMostPopularFilms(int counter) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing(f -> f.getLikes().size(), Comparator.reverseOrder()))
                .limit(counter)
                .collect(Collectors.toList());
    }

    public Film getFilm(String id) {
        return getStoredFilm(id);
    }

    private void validate(Film film) {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации Фильма: " + violations);
        }
        if (!film.getReleaseDate().isAfter(MIN_REALIZE_DATE) && !film.getDescription().isEmpty()) {
            throw new ValidationException("Ошибка валидации Фильма: " + violations);
        }
        if (film.getId() == 0) {
            film.setId(getNextId());
        }
    }

    public void checkExistence(Film film) {
        if (!getFilms().contains(film)) {
            throw new ObjectNotFoundException("Ошибка, фильм не найден");
        }
    }

    private static int getNextId() {
        return increment++;
    }

    private Integer intFromString(final String supposedInt) {
        try {
            return Integer.valueOf(supposedInt);
        } catch (NumberFormatException exception) {
            return Integer.MIN_VALUE;
        }
    }

    private Film getStoredFilm(final String supposedId) {
        final int filmId = intFromString(supposedId);
        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            throw new ObjectNotFoundException("Фильм с идентификатором " +
                    filmId + " не зарегистрирован!");
        }
        return film;
    }

    public Collection<Genre> getAllGenres() {
        return genreDbStorage.getAllGenres();
    }

    public Genre getGenre(int id) {
        return genreDbStorage.getGenreById(id);
    }

    public Collection<Mpa> getAllMpa() {
        return mpaDbStorage.getAllMpa();
    }

    public Mpa getMpa(int id) {
        return mpaDbStorage.getMpaById(id);
    }
}
