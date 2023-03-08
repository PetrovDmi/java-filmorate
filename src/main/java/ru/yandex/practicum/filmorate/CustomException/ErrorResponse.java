package ru.yandex.practicum.filmorate.CustomException;

public class ErrorResponse {
    String description;

    public ErrorResponse(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
