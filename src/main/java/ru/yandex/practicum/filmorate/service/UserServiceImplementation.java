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
        return userStorage.findAll();
    }

    @Override
    public User create(User user) {
        return userStorage.create(user);
    }

    @Override
    public User update(User newUser) {
        Long id = newUser.getId();

        if (userStorage.findById(id).isEmpty()) {
            String errorMessage = String.format("Пользователь с id %d не найден.", id);
            throw new NotFoundException(errorMessage);
        }

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
                        String.format("Пользователь с userId %d не найден.", id)));

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
                        String.format("Пользователь с userId %d не найден.", id)));

        if (!user.getFriends().contains(friendId)) {
            throw new FriendNotFoundException(
                    String.format("friendId %d не найден в списке друзей пользователя.", friendId));
        }

        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с friendId %d не найден.", friendId)));

        user.removeFriendId(friendId);
        friend.removeFriendId(id);
        log.info("Пользователь с id {} удалил из друзей пользователя с friendId {}.", id, friendId);
        return user;
    }

    @Override
    public void clear() {
        userStorage.clear();
    }
}
