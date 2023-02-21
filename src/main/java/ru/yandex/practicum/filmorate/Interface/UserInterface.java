package ru.yandex.practicum.filmorate.Interface;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserInterface {
    Collection<User> findAll();
    User create(User user);
    User put(User user);
}
