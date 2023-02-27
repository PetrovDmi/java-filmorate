package ru.yandex.practicum.filmorate.CustomException;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
