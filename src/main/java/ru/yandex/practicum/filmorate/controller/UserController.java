package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.CustomException.ErrorResponse;
import ru.yandex.practicum.filmorate.CustomException.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.CustomException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController extends Controller<User> {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable String id) {
        log.info("Получен запрос GET к эндпоинту: /users/{}/", id);
        return userService.getUser(id);
    }

    @Override
    @GetMapping
    public Collection<User> getAll() {
        log.info("Получен запрос GET к эндпоинту: /users");
        return userService.getAllUsers();
    }

    @Override
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос POST. Данные тела запроса: {}", user);
        userService.add(user);
        log.info("Создан объект {} с идентификатором {}", User.class.getSimpleName(), user.getId());
        return user;
    }

    @Override
    @PutMapping
    public User put(@Valid @RequestBody User user) {
        log.info("Получен запрос PUT. Данные тела запроса: {}", user);
        userService.update(user);
        log.info("Обновлен объект {} с идентификатором {}", User.class.getSimpleName(), user.getId());
        return user;
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findFriends(@PathVariable String id) {
        log.info("Получен запрос GET к эндпоинту: /users/{}/friends", id);
        return userService.getFriends(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable String id, @PathVariable String friendId) {
        log.info("Получен запрос PUT к эндпоинту: /users/{}/friends/{}", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Обновлен объект {} с идентификатором {}. Добавлен друг {}", User.class.getSimpleName(), id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable String id, @PathVariable String otherId) {
        log.info("Получен запрос GET к эндпоинту: /users/{}/friends/common/{}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable String id, @PathVariable String friendId) {
        log.info("Получен запрос DELETE к эндпоинту: /users/{}/friends/{}", id, friendId);
        userService.deleteFriend(id, friendId);
        log.info("Обновлен объект {} с идентификатором {}. Удален друг {}", User.class.getSimpleName(), id, friendId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFound(final ObjectNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }
}
