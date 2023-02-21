package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.CustomException.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.CustomException.ValidationException;
import ru.yandex.practicum.filmorate.Interface.FilmInterface;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController implements FilmInterface {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private final LocalDate MIN_REALIZE_DATE = LocalDate.of(1895, 12, 28);
    private Integer id = 1;

    public Integer getFilmId() {
        return id++;
    }

    public void validate(Film film) {
        if (film != null && film.getReleaseDate().isAfter(MIN_REALIZE_DATE) && !film.getDescription().isEmpty()) {
            if (film.getId() == 0) {
                film.setId(getFilmId());
            }
        } else {
            throw new ValidationException("Ошибка валидации");
        }
    }

    public void checkFilmExistence(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ObjectNotFoundException("Ошибка, фильм не найден");
        }
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получен запрос GET к эндпоинту: /films");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        log.info("Получен запрос POST. Данные тела запроса: {}", film);
        validate(film);
        films.put(film.getId(), film);
        log.info("Создан объект {} с идентификатором {}", Film.class.getSimpleName(), film.getId());
        return film;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) throws ObjectNotFoundException, ValidationException {
        log.info("Получен запрос PUT. Данные тела запроса: {}", film);
        checkFilmExistence(film);
        validate(film);
        films.put(film.getId(), film);
        log.info("Обновлен объект {} с идентификатором {}", Film.class.getSimpleName(), film.getId());
        return film;
    }

}

