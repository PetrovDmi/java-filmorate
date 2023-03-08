package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.CustomException.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.CustomException.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private int increment = 1;
    private final Validator validator;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(Validator validator, InMemoryUserStorage userStorage) {
        this.validator = validator;
        this.inMemoryUserStorage = userStorage;
    }

    public void add(final User user) {
        inMemoryUserStorage.addUser(validate(user));
    }

    public Collection<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    public void update(final User user) {
        checkExistence(user);
        inMemoryUserStorage.updateUser(validate(user));
    }

    public void addFriend(final String supposedUserId, final String supposedFriendId) {
        User user = getStoredUser(supposedUserId);
        User friend = getStoredUser(supposedFriendId);
        inMemoryUserStorage.addFriend(user.getId(), friend.getId());
    }

    public void deleteFriend(final String supposedUserId, final String supposedFriendId) {
        User user = getStoredUser(supposedUserId);
        User friend = getStoredUser(supposedFriendId);
        inMemoryUserStorage.deleteFriend(user.getId(), friend.getId());
    }

    public Collection<User> getFriends(final String supposedUserId) {
        User user = getStoredUser(supposedUserId);
        Collection<User> friends = new HashSet<>();
        for (Integer id : user.getFriends()) {
            friends.add(inMemoryUserStorage.getUser(id));
        }
        return friends;
    }

    public Collection<User> getCommonFriends(final String supposedUserId, final String supposedOtherId) {
        Collection<User> commonFriends;
        User user = getStoredUser(supposedUserId);
        User otherUser = getStoredUser(supposedOtherId);

        commonFriends = user.getFriends().stream()
                .filter(id -> otherUser.getFriends().contains(id))
                .map(inMemoryUserStorage::getUser)
                .collect(Collectors.toCollection(ArrayList::new));
        return commonFriends;
    }

    public User getUser(final String userId) {
        checkIdInStorage(Integer.parseInt(userId));
        return getStoredUser(userId);
    }

    public void checkIdInStorage(Integer userId) {
        if (userId > increment) {
            throw new ObjectNotFoundException("Ошибка получения пользователя по id");
        }
        if (!inMemoryUserStorage.getAllUsers().contains(getStoredUser(String.valueOf(userId)))) {
            throw new ObjectNotFoundException("Пользователь не найден!");
        }
        boolean isNotError = getAllUsers().stream()
                .anyMatch(user -> user.getId() == userId);
        if (!isNotError) {
            throw new ObjectNotFoundException("Пользователь не найден!");
        }
    }

    public void checkExistence(User user) {
        if (!getAllUsers().contains(user)) {
            throw new ObjectNotFoundException("Пользователь не найден!");
        }
    }

    private User validate(final User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (user == null) {
            throw new ValidationException("Ошибка валидации Пользователя: " + violations);
        }
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации Пользователя: " + violations);
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.info("UserService: Поле name не задано. Установлено значение {} из поля login", user.getLogin());
        } else if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("UserService: Поле name не содержит буквенных символов. " +
                    "Установлено значение {} из поля login", user.getLogin());
        }
        if (user.getId() == 0) {
            user.setId(increment++);
        }
        return user;
    }

    private Integer idFromString(final String supposedId) {
        try {
            return Integer.valueOf(supposedId);
        } catch (NumberFormatException exception) {
            return Integer.MIN_VALUE;
        }
    }

    private User getStoredUser(final String supposedId) {
        final int userId = idFromString(supposedId);
        if (userId == Integer.MIN_VALUE) {
            throw new ObjectNotFoundException("Не удалось распознать идентификатор пользователя: " + "значение " + supposedId);
        }
        User user = inMemoryUserStorage.getUser(userId);
        if (user == null) {
            throw new ObjectNotFoundException("Пользователь с идентификатором " + userId + " не зарегистрирован!");
        }
        return user;
    }
}