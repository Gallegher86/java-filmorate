package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.SelfFriendshipException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService{
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
        log.info("Обновленный пользователь с логином {} с id {} добавлен в список.", newUser.getLogin(), newUser.getId());
        return user;
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new SelfFriendshipException("Невозможно добавить себя в друзья.");
        }

        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с userId %d не найден.", userId)));

        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с friendId %d не найден.", friendId)));

        user.addFriendId(friendId);
        friend.addFriendId(userId);
        return user;
    }

    @Override
    public void clear() {
        userStorage.clear();
    }
}
