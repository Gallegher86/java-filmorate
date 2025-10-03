package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 1L;

    @GetMapping
    public List<User> findAll() {
        log.info("Получен запрос на получение списка пользователей.");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос на добавление пользователя с логином {}.", user.getLogin());
        long id = generateNextId();
        log.trace("Сгенерирован новый id для пользователя {}", id);
        user.setId(id);
        users.put(id, user);
        log.info("Пользователь с логином {} с id {} добавлен в список.", user.getLogin(), user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя с логином {} с id {}.", newUser.getLogin(), newUser.getId());
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

    private Long generateNextId() {
        return idCounter++;
    }
}
