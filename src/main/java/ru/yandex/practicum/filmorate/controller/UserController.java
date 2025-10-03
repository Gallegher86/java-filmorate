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
    public List findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        long id = generateNextId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        Long id = newUser.getId();

        User oldUser = users.get(id);
        if (Objects.isNull(oldUser)) {
            String errorMessage = String.format("Пользователь с id %d не найден.", id);
            throw new NotFoundException(errorMessage);
        }

        users.put(id, newUser);
        return newUser;
    }

    private Long generateNextId() {
        return idCounter++;
    }
}
