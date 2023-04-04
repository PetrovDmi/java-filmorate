package ru.yandex.practicum.filmorate.CustomException;

public class InternalServerError extends RuntimeException {
    public InternalServerError(String message) {
        super(message);
    }
}
