package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.CustomException.ErrorResponse;
import ru.yandex.practicum.filmorate.CustomException.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.DAO.GenreDbStorage;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/genres")
public class GenreController {
    private final FilmService filmService;
    private final GenreDbStorage genreDbStorage;

    public GenreController(FilmService filmService, GenreDbStorage genreDbStorage) {
        this.filmService = filmService;
        this.genreDbStorage = genreDbStorage;
    }

    @GetMapping
    public Collection<Genre> findAll() {
        log.info("Получен запрос GET к эндпоинту: /genres");
        return filmService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre findGenre(@PathVariable long id) {
        log.info("Получен запрос GET к эндпоинту: /genres/{}", id);
        return genreDbStorage.getGenreById((int) id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFound(final ObjectNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}