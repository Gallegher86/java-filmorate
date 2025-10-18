package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        log.info("Получен запрос на получение списка фильмов.");
        return filmService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма {}.", film.getName());
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Получен запрос на обновление фильма {} с id {}.", newFilm.getName(), newFilm.getId());
        return filmService.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос на добавление лайка фильму с id {} от пользователя с userId {}.", id, userId);
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос на удаление лайка фильму с id {} от пользователя с userId {}.", id, userId);
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular?count={count}")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") long count) {
        log.info("Получен запрос на получение {} популярных фильмов.", count);
        return filmService.getPopularFilms(count);
    }
}
