package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/mpa")
public class MpaController {
    private final FilmService filmService;

    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Mpa> findAll() {
        log.info("Получен запрос GET к эндпоинту: /mpa");
        return filmService.getAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa findGenre(@PathVariable String id) {
        log.info("Получен запрос GET к эндпоинту: /mpa/{}", id);
        return filmService.getMpa(Integer.parseInt(id));
    }
}

