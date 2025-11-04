package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.storage.FilmStorage;

import java.util.List;

@Slf4j
@Service
public class FilmServiceImplementation implements FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmServiceImplementation(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                                     UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = filmStorage.findAll();
        log.info("Список фильмов выдан.");
        return films;
    }

    @Override
    public Film findById(Long id) {
        Film film = filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Фильм с id %d не найден.", id)));

        log.info("Фильм с id {} выдан.", id);
        return film;
    }

    @Override
    public Film create(Film newFilm) {


        Film film = filmStorage.create(newFilm);
        log.info("Фильм {} с id {} помещен  в коллекцию.", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film update(Film updatedFilm) {
        checkFilmId(updatedFilm.getId());

        Film film = filmStorage.update(updatedFilm);
        log.info("Обновленный фильм {} с id {} помещен  в коллекцию.", film.getName(), film.getId());
        return film;
    }

    @Override
    public void addLike(Long id, Long userId) {
        userService.checkUserId(userId);
        checkFilmId(id);
        filmStorage.addLike(id, userId);
        log.info("Пользователь с userId {} поставил лайк фильму с id {}.", userId, id);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        userService.checkUserId(userId);
        checkFilmId(id);
        filmStorage.deleteLike(id, userId);
        log.info("Пользователь с userId {} удалил лайк фильму с id {}.", userId, id);
    }

    @Override
    public List<Film> getPopularFilms(long count) {
        if (count < 0) {
            throw new IllegalArgumentException("Параметр {count} не может быть отрицательным.");
        }
        List<Film> films = filmStorage.findPopular(count);
        log.info("Создан список {} лучших фильмов.", count);
        return films;
    }

    @Override
    public void checkFilmId(Long id) {
        if (!filmStorage.existsById(id)) {
            String errorMessage = String.format("Фильм с id %d не найден.", id);
            throw new NotFoundException(errorMessage);
        }
    }
}
