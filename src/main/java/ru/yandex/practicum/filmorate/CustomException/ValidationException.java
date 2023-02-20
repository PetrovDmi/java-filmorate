package ru.yandex.practicum.filmorate.CustomException;

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
