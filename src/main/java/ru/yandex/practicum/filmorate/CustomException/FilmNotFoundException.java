package ru.yandex.practicum.filmorate.CustomException;

public class FilmNotFoundException extends Exception{
    public FilmNotFoundException(String message) {
        super(message);
    }
}
