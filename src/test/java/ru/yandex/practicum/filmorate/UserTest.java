package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.CustomException.ValidationException;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserTest {
    private Validator validator;
    private User user;
    UserController userController;

    @BeforeEach
    public void initBeforeEach() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
        userController = new UserController();
        user = new User(1, "login", "name", "yandex@mail.ru", LocalDate.of(2000, 8, 20));
    }

    @Test
    void validateNameWithSpaceShouldReturnError() {
        try {
            User user = new User(1, "dolore ullamco", "name", "yandex@mail.ru", LocalDate.of(2446, 8, 20));
        } catch (Exception e) {
            String error = e.getMessage();
            assertEquals("В логине содержится пробел", error);
        }
    }

    @Test
    void validateIdNegativeShouldFailValidation() {
        user.setId(-1);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
    }

    @Test
    void validateIdZeroShouldNotFailValidation() {
        user.setId(0);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size());
    }

    @Test
    void createUserIdZeroShouldGiveNewIdToUser() throws ValidationException {
        user.setId(0);

        userController.create(user);

        Assertions.assertEquals(1, userController.findAll().iterator().next().getId());
    }

    @Test
    void validateLoginVoidShouldFailValidation() {
        user.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
    }

    @Test
    void createUserHaveNotNameShouldSetNameAsLogin() throws ValidationException {
        user.setName("");

        userController.create(user);

        Assertions.assertEquals(user.getLogin(), userController.findAll().iterator().next().getName());
    }

    @Test
    void validateMailNotContainsMailSymbolShouldFailValidation() {
        user.setEmail("mail");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
    }

    @Test
    void validateMailIsVoidShouldFailValidation() {
        user.setEmail("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
    }

    @Test
    void validateBirthdayDateInFutureShouldFailValidation() {
        user.setBirthday(LocalDate.of(2030, 8, 20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
    }

    @Test
    void validateVoidUserShouldFailValidation() {
        user = null;

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> userController.create(user));

        Assertions.assertEquals("Ошибка валидации", exception.getMessage());
    }

    @Test
    void validatePutUserWithNoContainsIdShouldFailValidation() throws ValidationException {
        userController.create(user);
        user.setBirthday(LocalDate.of(2030, 8, 20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
    }

    @Test
    void validatePutUserWithSpaceInLoginShouldFailValidation() throws ValidationException {
        userController.create(user);
        user.setLogin("log in");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
    }

    @Test
    void putUserHaveNotNameShouldSetNameAsLogin() throws ValidationException {
        userController.create(user);
        user.setName("");

        userController.put(user);

        Assertions.assertEquals(user.getLogin(), userController.findAll().iterator().next().getName());
    }
}
