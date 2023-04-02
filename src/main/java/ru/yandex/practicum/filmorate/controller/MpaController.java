package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.CustomException.ErrorResponse;
import ru.yandex.practicum.filmorate.CustomException.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.DAO.MpaDbStorage;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/mpa")
public class MpaController {
    private final FilmService filmService;
    private final MpaDbStorage mpaDbStorage;

    public MpaController(FilmService filmService, MpaDbStorage mpaDbStorage) {
        this.filmService = filmService;
        this.mpaDbStorage = mpaDbStorage;
    }

    @GetMapping
    public Collection<Mpa> findAll() {
        log.info("Получен запрос GET к эндпоинту: /mpa");
        return filmService.getAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa findGenre(@PathVariable long id) {
        log.info("Получен запрос GET к эндпоинту: /mpa/{}", id);
        return mpaDbStorage.getMpaById((int) id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFound(final ObjectNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}

