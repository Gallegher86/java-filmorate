package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findById(Long id);

    User create(User newUser);

    User update(User updatedUser);

    User addFriend(Long id, Long friendId);

    User removeFriend(Long id, Long friendId);

    List<User> findFriends(Long id);

    List<User> findCommonFriends(Long id, Long otherId);

    void checkUserId(Long id);
}
