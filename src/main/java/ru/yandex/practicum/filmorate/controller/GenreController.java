package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/genres")
public class GenreController {
    private final FilmService filmService;

    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }


    @GetMapping
    public Collection<Genre> findAll() {
        log.info("Получен запрос GET к эндпоинту: /genres");
        return filmService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre findGenre(@PathVariable String id) {
        log.info("Получен запрос GET к эндпоинту: /genres/{}", id);
        return filmService.getGenre(Integer.parseInt(id));
    }
}