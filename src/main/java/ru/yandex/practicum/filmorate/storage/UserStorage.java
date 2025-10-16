package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();

    Optional<User> findById(Long id);

    List<User> findFriends(Long id);

    User create(User user);

    User save(User newUser);

    void clear();
}
