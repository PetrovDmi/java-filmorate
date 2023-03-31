package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.CustomException.InternalServerError;
import ru.yandex.practicum.filmorate.CustomException.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film getFilm(int filmId) {
        return films.get(filmId);
    }

    @Override
    public Collection<Film> getAllFilms() {
        Collection<Film> allFilms = films.values();
        if (allFilms.isEmpty()) {
            allFilms.addAll(films.values());
        }
        return allFilms;
    }

    @Override
    public Film addFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!getAllFilms().contains(film)) {
            throw new ObjectNotFoundException("Фильм с идентификатором " + film.getId() + " не зарегистрирован!");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(Film film) {
        films.remove(film.getId());
        if (films.containsKey(film.getId())) {
            throw new InternalServerError("Ошибка удаления фильма с идентификатором " + film.getId());
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        checkFilmExistence(filmId);
        Film film = films.get(filmId);
        film.addLike(userId);
        checkLikeMark(filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        checkFilmExistence(filmId);
        checkLikeMark(filmId, userId);
        Film film = films.get(filmId);
        film.getLikes().remove(userId);
    }

    public void checkLikeMark (int filmId, int userId) {
        Film film = films.get(filmId);
        if (!film.getLikes().contains(userId)) {
            throw new ObjectNotFoundException("Лайк не найден!");
        }
    }

    public void checkFilmExistence (int filmId) {
        if (!films.containsKey(filmId)) {
            throw new ObjectNotFoundException("Фильм не найден!");
        }
    }

    public Collection<Film> getMostPopularFilms(int size) {
        return getAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(size)
                .collect(Collectors.toSet());
    }
}
