package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    public List<User> findAll();

    public Optional<User> findById(Long id);

    public User create(User user);

    public User save(User newUser);

    public void clear();
}
