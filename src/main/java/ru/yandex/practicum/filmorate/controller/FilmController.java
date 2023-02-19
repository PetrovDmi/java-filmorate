package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private Integer id = 1;

    private Integer getFilmId (){
        return id++;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получен запрос GET к эндпоинту: /films");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен запрос POST. Данные тела запроса: {}", film);
        if (film != null && film.getReleaseDate().isAfter(LocalDate.of(1895,12, 28))){
            if (film.getId() <= 0){
                film.setId(getFilmId());
                films.put(film.getId(), film);
                log.info("Создан объект {} с идентификатором {}", User.class.getSimpleName(), film.getId());
                return film;
            }
            films.put(film.getId(), film);
            log.info("Создан объект {} с идентификатором {}", Film.class.getSimpleName(), film.getId());
        } else {
            throw new RuntimeException("Ошибка валидации фильма");
        }
        return film;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        log.info("Получен запрос PUT. Данные тела запроса: {}", film);
        if (!films.containsKey(film.getId())) {
            throw new RuntimeException("Фильм не существует");
        }
        films.put(film.getId(), film);
        log.info("Обновлен объект {} с идентификатором {}", Film.class.getSimpleName(), film.getId());
        return film;
    }

}

