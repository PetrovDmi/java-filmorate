package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FilmorateApplicationTests {
    private Validator validator;
    private Film film;

    @BeforeEach
    public void initBeforeEach() {
        UserController userController = new UserController();
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = (Validator) validatorFactory.usingContext().getValidator();
        FilmController filmController = new FilmController();
        film = new Film(1, "Тихоокеанский рубеж", "О роботах",
                LocalDate.of(2013, 6, 11), 131);
    }


    @Test
    void contextLoads() {
    }

    @Test
    void loginContainsSpaceGet500Response() {
        try {
            User user = new User(1, "dolore ullamco", "name", "yandex@mail.ru", LocalDate.of(2446, 8, 20));
        } catch (Exception e) {
            String error = e.getMessage();
            assertEquals("В логине содержится пробел", error);
        }
    }

    @Test
    void validateIdNegativeShouldFailValidation() {
        film = new Film(-1, "Тихоокеанский рубеж", "О роботах",
                LocalDate.of(2013, 6, 11), 131);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
    }
}
