package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User getUser(final Integer id);

    Collection<User> getAllUsers();

    void addUser(User user);

    void updateUser(User user);
}

