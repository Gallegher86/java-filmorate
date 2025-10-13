package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 1L;

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User create(User user) {
        long id = generateNextId();
        log.trace("Сгенерирован новый id для пользователя {}", id);
        user.setId(id);
        users.put(id, user);
        log.info("Пользователь с логином {} с id {} добавлен в список.", user.getLogin(), user.getId());
        return user;
    }

    public User update(User newUser) {
        Long id = newUser.getId();

        User oldUser = users.get(id);
        if (Objects.isNull(oldUser)) {
            String errorMessage = String.format("Пользователь с id %d не найден.", id);
            throw new NotFoundException(errorMessage);
        }

        users.put(id, newUser);
        log.info("Обновленный пользователь с логином {} с id {} добавлен в список.", newUser.getLogin(), newUser.getId());
        return newUser;
    }

    public void clear() {
        users.clear();
        idCounter = 1L;
    }

    private Long generateNextId() {
        return idCounter++;
    }
}
