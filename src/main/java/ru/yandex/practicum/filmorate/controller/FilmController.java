package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private Long idCounter = 1L;

    @GetMapping
    public Collection<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        long id = generateNextId();
        film.setId(id);
        films.put(id, film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        Long id = newFilm.getId();

        Film oldFilm = films.get(id);
        if (Objects.isNull(oldFilm)) {
            String errorMessage = String.format("Фильм с id %d не найден.", id);
            throw new NotFoundException(errorMessage);
        }

        films.put(id, newFilm);
        return newFilm;
    }

    private Long generateNextId() {
        return idCounter++;
    }
}
