package ru.yandex.practicum.filmorate.Interface;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;

public interface UserInterface {
    Collection<User> findAll();
    User create(User user);
    User put(User user);
}
