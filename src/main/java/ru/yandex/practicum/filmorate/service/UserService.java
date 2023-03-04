package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.CustomException.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private int increment = 0;
    private final Validator validator;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(Validator validator, InMemoryUserStorage userStorage) {
        this.validator = validator;
        this.inMemoryUserStorage = userStorage;
    }

    public User add(final User user) {
        validate(user);
        return inMemoryUserStorage.addUser(user);
    }

    public Collection<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    public User update(final User user) {
        validate(user);
        return inMemoryUserStorage.updateUser(user);
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
        Collection<User> commonFriends = new HashSet<>();
        User user = getStoredUser(supposedUserId);
        if (user.getFriends() == null) {
            return commonFriends;
        }
        User otherUser = getStoredUser(supposedOtherId);
        for (Integer id : user.getFriends()) {
            if (otherUser.getFriends().contains(id)) {
                commonFriends.add(inMemoryUserStorage.getUser(id));
            }
        }
        return commonFriends;
    }

    public User getUser(final String userId) {
        return getStoredUser(userId);
    }

    private void validate(final User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.info("UserService: Поле name не задано. Установлено значение {} из поля login", user.getLogin());
        } else if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("UserService: Поле name не содержит буквенных символов. " +
                    "Установлено значение {} из поля login", user.getLogin());
        }
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации Пользователя: " + violations);
        }
        if (user.getId() == 0) {
            user.setId(increment++);
        }
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