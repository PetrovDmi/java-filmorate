package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private Integer id = 1;

    private Integer getUserId(){
        return id++;
    }
    public Boolean loginContainsSpace(User user){
        if (user.getLogin().contains(" ")){
            throw new RuntimeException("В логине содержится пробел");
        }
        return false;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен запрос GET к эндпоинту: /users");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос POST. Данные тела запроса: {}", user);

        if (user != null && !loginContainsSpace(user)) {
            if (user.getId() == 0){
                user.setId(getUserId());
                if (user.getName() == null || user.getName().equals("")){
                    user.setName(user.getLogin());
                }
                users.put(user.getId(), user);
                log.info("Создан объект {} с идентификатором {}", User.class.getSimpleName(), user.getId());
                return user;
            }
            user.setId(getUserId());
            users.put(user.getId(), user);
            log.info("Создан объект {} с идентификатором {}", User.class.getSimpleName(), user.getId());
        } else {
            throw new RuntimeException("Ошибка валидации");
        }
        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        log.info("Получен запрос PUT. Данные тела запроса: {}", user);
        if (!users.containsKey(user.getId())) {
            throw new RuntimeException("Пользователь не существует");
        }
        if (user.getName() == null || user.getName().equals("")){
            user.setName(user.getLogin());
        }
        if (!loginContainsSpace(user)) {
            users.put(user.getId(), user);
            log.info("Обновлен объект {} с идентификатором {}", User.class.getSimpleName(), user.getId());
        }
        return user;
    }

}
