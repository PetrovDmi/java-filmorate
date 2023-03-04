package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.CustomException.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Set;

@Service
public class FilmService {
    private static int increment = 0;
    private final Validator validator;
    private final InMemoryFilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(Validator validator, InMemoryFilmStorage filmStorage,
                       @Autowired(required = false) UserService userService) {
        this.validator = validator;
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getAllFilms();
    }

    public Film add(Film film) {
        validate(film);
        return filmStorage.addFilm(film);
    }

    public Film update(Film film) {
        validate(film);
        return filmStorage.updateFilm(film);
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

    public Collection<Film> getMostPopularFilms(final String count) {
        Integer size = intFromString(count);
        if (size == Integer.MIN_VALUE) {
            size = 10;
        }
        return filmStorage.getMostPopularFilms(size);
    }

    public Film getFilm(String id) {
        return getStoredFilm(id);
    }

    private void validate(Film film) {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации Фильма: " + violations);
        }
        if (film.getId() == 0) {
            film.setId(getNextId());
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
        if (filmId == Integer.MIN_VALUE) {
            throw new ObjectNotFoundException("Не удалось распознать идентификатор фильма: " +
                    "значение " + supposedId);
        }
        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            throw new ObjectNotFoundException("Фильм с идентификатором " +
                    filmId + " не зарегистрирован!");
        }
        return film;
    }
}
