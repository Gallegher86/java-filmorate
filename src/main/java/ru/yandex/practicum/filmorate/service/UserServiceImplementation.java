package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserServiceImplementation implements UserService {
    private final UserStorage userStorage;

    public UserServiceImplementation(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<User> findAll() {
        List<User> users = userStorage.findAll();
        log.info("Список пользователей выдан.");
        return users;
    }

    @Override
    public User findById(Long id) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с id %d не найден.", id)));

        log.info("Пользователь с id {} выдан.", id);
        return user;
    }

    @Override
    public User create(User newUser) {
        User user = userStorage.create(newUser);
        log.info("Пользователь с логином {} с id {} добавлен в список.", user.getLogin(), user.getId());
        return user;
    }

    @Override
    public User update(User updatedUser) {
        checkUserId(updatedUser.getId());

        User user = userStorage.update(updatedUser);
        log.info("Обновленный пользователь с логином {} с id {} добавлен в список.",
                user.getLogin(), user.getId());
        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException(
                    String.format("Пользователь с id %d не может быть добавлен в друзья к самому себе.", userId));
        }

        checkUserId(userId);
        checkUserId(friendId);

        boolean directExists = userStorage.friendshipExists(userId, friendId);
        boolean reverseExists = userStorage.friendshipExists(friendId, userId);

        if (directExists) {
            log.trace("Получен повторный запрос на добавление в друзья id {} с friendId {}.", userId, friendId);
            return;
        }

        if (reverseExists) {
            userStorage.addFriend(userId, friendId, FriendStatus.CONFIRMED);
            userStorage.updateFriend(friendId, userId, FriendStatus.CONFIRMED);
            log.info("Пользователи с id {} и {} стали друзьями.", userId, friendId);
            return;
        }

        userStorage.addFriend(userId, friendId, FriendStatus.PENDING);
        log.info("Пользователь с id {} отправил заявку в друзья пользователю с id {}.", userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        checkUserId(userId);
        checkUserId(friendId);

        if (!userStorage.friendshipExists(userId, friendId)) {
            log.trace("Получен запрос на удаление не существующей дружбы userId {} с friendId {}.", userId, friendId);
            return;
        }

        userStorage.removeFriend(userId, friendId);

        if (userStorage.friendshipExists(friendId, userId)) {
            userStorage.updateFriend(friendId, userId, FriendStatus.PENDING);
        }

        log.info("Пользователь с id {} удалил из друзей пользователя с friendId {}.", userId, friendId);
    }

    @Override
    public List<User> findFriends(Long id) {
        checkUserId(id);

        List<User> friends = userStorage.findFriends(id);
        log.info("Список друзей пользователя с id {} выдан.", id);
        return friends;
    }

    @Override
    public List<User> findCommonFriends(Long id, Long otherId) {
        checkUserId(id);
        checkUserId(otherId);
        List<User> commonFriends = userStorage.findCommonFriends(id, otherId);
        log.info("Список общих друзей пользователя с id {} и пользователя с otherId {} выдан.", id, otherId);
        return commonFriends;
    }

    @Override
    public void checkUserId(Long id) {
        if (!userStorage.existsById(id)) {
            String errorMessage = String.format("Пользователь с id %d не найден.", id);
            throw new NotFoundException(errorMessage);
        }
    }
}
