package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.CustomException.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.CustomException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController extends Controller<User> {
    private final HashMap<Integer, User> users = new HashMap<>();
    private Integer id = 1;

    @Override
    @GetMapping
    public Collection<User> getAll() {
        log.info("Получен запрос GET к эндпоинту: /users");
        return users.values();
    }

    @Override
    @PostMapping
    public User create(@Valid @RequestBody User user) throws ValidationException {
        log.info("Получен запрос POST. Данные тела запроса: {}", user);
        validate(user);
        users.put(user.getId(), user);
        log.info("Создан объект {} с идентификатором {}", User.class.getSimpleName(), user.getId());
        return user;
    }

    @Override
    @PutMapping
    public User put(@Valid @RequestBody User user) throws ValidationException {
        log.info("Получен запрос PUT. Данные тела запроса: {}", user);
        checkExistence(user);
        validate(user);
        users.put(user.getId(), user);
        log.info("Обновлен объект {} с идентификатором {}", User.class.getSimpleName(), user.getId());
        return user;
    }

    public Integer getId() {
        return id++;
    }

    public void validate(User user) {
        if (user != null) {
            if (user.getId() == 0) {
                user.setId(getId());
            }
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
        } else {
            throw new ValidationException("Ошибка валидации");
        }
    }

    public void checkExistence(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ObjectNotFoundException("Ошибка, пользователь не найден");
        }
    }
}
