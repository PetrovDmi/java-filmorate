package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.CustomException.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.CustomException.ValidationException;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FilmTest {
    private Validator validator;
    private Film film;
    FilmController filmController;

    @BeforeEach
    public void initBeforeEach() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
        filmController = new FilmController();
        film = new Film(1, "Тихоокеанский рубеж", "О роботах",
                LocalDate.of(2013, 6, 11), 131);
    }

    @Test
    void validateIdNegativeShouldFailValidation() {
        film = new Film(-1, "Тихоокеанский рубеж", "О роботах",
                LocalDate.of(2013, 6, 11), 131);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
    }

    @Test
    void validateIdZeroShouldNotFailValidation() {
        film = new Film(0, "Тихоокеанский рубеж", "О роботах",
                LocalDate.of(2013, 6, 11), 131);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(0, violations.size());
    }

    @Test
    void validateDurationNegativeShouldFailValidation() {
        film = new Film(1, "Тихоокеанский рубеж", "О роботах",
                LocalDate.of(2013, 6, 11), -131);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
    }

    @Test
    void validateDurationZeroShouldFailValidation() {
        film = new Film(1, "Тихоокеанский рубеж", "О роботах",
                LocalDate.of(2013, 6, 11), 0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
    }

    @Test
    void validateNameEmptyShouldFailValidation() {
        film.setName(" ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
    }

    @Test
    void validateDescriptionEmptyShouldFailValidation() {
        film.setDescription("");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
    }

    @Test
    void addFilmWithBadDescriptionShouldThrowCustomValidationException() {
        film.setDescription("1".repeat(300));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
    }

    @Test
    void addFilmWithBadReleaseDateShouldThrowCustomValidationException() {
        film.setReleaseDate(LocalDate.of(1800, 12, 14));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> filmController.create(film));

        Assertions.assertEquals("Ошибка валидации", exception.getMessage());
    }

    @Test
    void getAllFilmsTestShouldReturnArrayListOfFilms() throws ValidationException {
        Collection<Film> films;

        filmController.create(film);
        films = filmController.getAllFilms();

        Assertions.assertEquals(1, films.size());
    }

    @Test
    void addFilmTestShouldAddFilm() throws ValidationException {
        filmController.create(film);

        Assertions.assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void updateFilmTestShouldAddFilm() throws ValidationException, ObjectNotFoundException {
        Film gottenFilm;
        filmController.create(film);
        film = new Film(1, "Тихоокеанский рубеж 2", "О роботах",
                LocalDate.of(2013, 6, 11), 131);

        filmController.put(film);
        gottenFilm = filmController.getAllFilms().iterator().next();

        Assertions.assertEquals("Тихоокеанский рубеж 2", gottenFilm.getName());
    }

    @Test
    void updateFilmWithBadParametersShouldThrowCustomValidationException() throws ValidationException {
        filmController.create(film);
        film = new Film(1, "Тихоокеанский рубеж 2", "О роботах",
                LocalDate.of(1800, 6, 11), 131);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> filmController.put(film));

        Assertions.assertEquals("Ошибка валидации", exception.getMessage());
    }

    @Test
    void updateFilmNotExistParametersShouldThrowFilmNotFoundException() throws ValidationException {
        filmController.create(film);
        film = new Film(5, "Тихоокеанский рубеж 2", "О роботах",
                LocalDate.of(1900, 6, 11), 131);

        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class, () -> filmController.put(film));

        Assertions.assertEquals("Ошибка, фильм не найден", exception.getMessage());
    }
}
