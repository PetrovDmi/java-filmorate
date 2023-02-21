package ru.yandex.practicum.filmorate.Interface;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmInterface {
    Collection<Film> getAllFilms();
    Film create(Film film);
    Film put(Film film);
}
