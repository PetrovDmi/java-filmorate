package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.CustomException.InternalServerError;
import ru.yandex.practicum.filmorate.CustomException.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User getUser(final Integer id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        Collection<User> allUsers = users.values();
        if (allUsers.isEmpty()) {
            allUsers.addAll(users.values());
        }
        return allUsers;
    }

    @Override
    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void updateUser(User user) {
        if (!getAllUsers().contains(user)) {
            throw new ObjectNotFoundException("Пользователь с идентификатором " +
                    user.getId() + " не зарегистрирован!");
        }
        users.put(user.getId(), user);
    }

    public void deleteUser(User user) {
        users.remove(user.getId());
        if (users.containsKey(user.getId())) {
            throw new InternalServerError("Ошибка удаления пользователя с идентификатором " + user.getId());
        }
    }

    public void addFriend(int userId, int friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        if (!checkFriendIsFriendToEachOther(userId, friendId)) {
            user.addFriend(friendId);
            friend.addFriend(userId);
        } else {
            throw new InternalServerError("Ошибка добавления друга");
        }
    }

    public void deleteFriend(int userId, int friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        if (checkFriendIsFriendToEachOther(userId, friendId)) {
            user.deleteFriend(friendId);
            friend.deleteFriend(userId);
        } else {
            throw new InternalServerError("Ошибка удаления друга");
        }
    }

    private boolean checkFriendIsFriendToEachOther(int userId, int friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        return user.getFriends().contains(friendId) && friend.getFriends().contains(userId);
    }
}
