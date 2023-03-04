package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.CustomException.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.CustomException.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController extends Controller<Film> {
    private final FilmService filmService;
    private final LocalDate MIN_REALIZE_DATE = LocalDate.of(1895, 12, 28);
    private Integer id = 1;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Получен запрос GET к эндпоинту: /films");
        return filmService.getFilms();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        log.info("Получен запрос POST. Данные тела запроса: {}", film);
        validate(film);
        filmService.add(film);
        log.info("Создан объект {} с идентификатором {}", Film.class.getSimpleName(), film.getId());
        return film;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) throws ObjectNotFoundException, ValidationException {
        log.info("Получен запрос PUT. Данные тела запроса: {}", film);
        checkExistence(film);
        validate(film);
        filmService.update(film);
        log.info("Обновлен объект {} с идентификатором {}", Film.class.getSimpleName(), film.getId());
        return film;
    }

    @GetMapping("/{id}")
    public Film getFromId(@PathVariable String id) {
        log.info("Получен запрос GET к эндпоинту: /films/{}", id);
        return filmService.getFilm(id);
    }

    @GetMapping({"/popular?count={count}", "/popular"})
    public Collection<Film> getMostPopular(@RequestParam(defaultValue = "10") String count) {
        log.info("Получен запрос GET к эндпоинту: /films/popular?count={}", count);
        return filmService.getMostPopularFilms(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable String id, @PathVariable String userId) {
        log.info("Получен запрос PUT к эндпоинту: /films/{}/like/{}", id, userId);
        filmService.addLike(id, userId);
        log.info("Обновлен объект {} с идентификатором {}, добавлен лайк от пользователя {}",
                Film.class.getSimpleName(), id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable String id, @PathVariable String userId) {
        log.info("Получен запрос DELETE к эндпоинту: films/{}/like/{}", id, userId);
        filmService.deleteLike(id, userId);
        log.info("Обновлен объект {} с идентификатором {}, удален лайк от пользователя {}",
                Film.class.getSimpleName(), id, userId);
    }

    public Integer getId() {
        return id++;
    }

    public void validate(Film film) {
        if (film != null && film.getReleaseDate().isAfter(MIN_REALIZE_DATE) && !film.getDescription().isEmpty()) {
            if (film.getId() == 0) {
                film.setId(getId());
            }
        } else {
            throw new ValidationException("Ошибка валидации");
        }
    }

    public void checkExistence(Film film) {
        if (!filmService.getFilms().contains(film)) {
            throw new ObjectNotFoundException("Ошибка, фильм не найден");
        }
    }
}

