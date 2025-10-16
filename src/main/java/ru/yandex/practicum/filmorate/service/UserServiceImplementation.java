package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.SelfFriendshipException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<User> findAll() {
        List<User> users = userStorage.findAll();
        log.info("Список пользователей выдан");
        return users;
    }

    @Override
    public User create(User user) {
        return userStorage.create(user);
    }

    @Override
    public User update(User newUser) {
        Long id = newUser.getId();

        checkId(id);

        User user = userStorage.save(newUser);
        log.info("Обновленный пользователь с логином {} с id {} добавлен в список.",
                newUser.getLogin(), newUser.getId());
        return user;
    }

    @Override
    public User addFriend(Long id, Long friendId) {
        if (id.equals(friendId)) {
            throw new SelfFriendshipException("Невозможно добавить себя в друзья.");
        }

        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с id %d не найден.", id)));

        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с friendId %d не найден.", friendId)));

        user.addFriendId(friendId);
        friend.addFriendId(id);
        log.info("Пользователь с id {} добавил в друзья пользователя с friendId {}.", id, friendId);
        return user;
    }

    @Override
    public User removeFriend(Long id, Long friendId) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с id %d не найден.", id)));

        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с friendId %d не найден.", friendId)));

        if (!user.getFriends().contains(friendId)) {
            throw new FriendNotFoundException(
                    String.format("friendId %d не найден в списке друзей пользователя.", friendId));
        }

        user.removeFriendId(friendId);
        friend.removeFriendId(id);
        log.info("Пользователь с id {} удалил из друзей пользователя с friendId {}.", id, friendId);
        return user;
    }

    @Override
    public List<User> findFriends(Long id) {
        checkId(id);

        List<User> friends = userStorage.findFriends(id);
        log.info("Список друзей пользователя с id {} выдан.", id);
        return friends;
    }

    @Override
    public List<User> findCommonFriends(Long id, Long otherId) {
        checkId(id);
        checkId(otherId);

        List<User> otherFriends = userStorage.findFriends(otherId);
        List<User> commonFriends = userStorage.findFriends(id).stream()
                .filter(otherFriends::contains)
                .toList();
        log.info("Список общих друзей пользователя с id {} и пользователя с otherId {} выдан.", id, otherId);
        return commonFriends;
    }

    @Override
    public void clear() {
        userStorage.clear();
    }

    private void checkId(Long id) {
        if (userStorage.findById(id).isEmpty()) {
            String errorMessage = String.format("Пользователь с id %d не найден.", id);
            throw new NotFoundException(errorMessage);
        }
    }
}
