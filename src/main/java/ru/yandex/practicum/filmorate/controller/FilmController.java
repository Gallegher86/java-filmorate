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
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private Long idCounter = 1L;

    @GetMapping
    public List<Film> findAll() {
        log.info("Получен запрос на получение списка фильмов.");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма {}.", film.getName());
        long id = generateNextId();
        log.trace("Сгенерирован новый id для фильма {}", id);
        film.setId(id);
        films.put(id, film);
        log.info("Фильм {} с id {} помещен  в коллекцию.", film.getName(), film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Получен запрос на обновление фильма {} с id {}.", newFilm.getName(), newFilm.getId());
        Long id = newFilm.getId();

        Film oldFilm = films.get(id);
        if (Objects.isNull(oldFilm)) {
            String errorMessage = String.format("Фильм с id %d не найден.", id);
            throw new NotFoundException(errorMessage);
        }

        films.put(id, newFilm);
        log.info("Обновленный фильм {} с id {} помещен  в коллекцию.", newFilm.getName(), newFilm.getId());
        return newFilm;
    }

    private Long generateNextId() {
        return idCounter++;
    }
}
