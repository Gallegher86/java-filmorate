package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 1L;

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(Long id) {
        User user = users.get(id);
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findFriends(Long id) {
        User user = users.get(id);
        return users.values().stream()
                .filter(friend -> user.getFriends().contains(friend.getId()))
                .toList();
    }

    @Override
    public User create(User user) {
        long id = generateNextId();
        log.trace("Сгенерирован новый id для пользователя {}", id);
        user.setId(id);
        users.put(id, user);
        log.info("Пользователь с логином {} с id {} добавлен в список.", user.getLogin(), user.getId());
        return user;
    }

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        log.trace("Пользователь с логином {} с id {} сохранен.", user.getLogin(), user.getId());
        return user;
    }

    @Override
    public void clear() {
        users.clear();
        idCounter = 1L;
        log.trace("UserStorage очищен.");
    }

    private Long generateNextId() {
        return idCounter++;
    }
}
