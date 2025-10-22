package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImplementation implements FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

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

        Film film = filmStorage.save(updatedFilm);
        log.info("Обновленный фильм {} с id {} помещен  в коллекцию.", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film addLike(Long id, Long userId) {
        userService.checkUserId(userId);

        Film film = filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Фильм с id %d не найден.", id)));

        film.addLikeId(userId);
        log.info("Пользователь с userId {} поставил лайк фильму с id {}.", userId, id);
        return film;
    }

    @Override
    public Film deleteLike(Long id, Long userId) {
        userService.checkUserId(userId);

        Film film = filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Фильм с id %d не найден.", id)));

        if (!film.getLikes().contains(userId)) {
            throw new IllegalArgumentException(
                    String.format("Пользователь с userId %d не ставил лайк фильму c id %d.", userId, id));
        }

        film.removeLikeId(userId);
        log.info("Пользователь с userId {} удалил лайк фильму с id {}.", userId, id);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(long count) {
        if (count < 0) {
            throw new IllegalArgumentException("Параметр {count} не может быть отрицательным.");
        }

        List<Film> topFilms = filmStorage.findAll().stream()
                .sorted(Comparator.comparingLong((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .toList();

        log.info("Фильмы отсортированы. Создан список {} лучших.", count);
        return topFilms;
    }

    @Override
    public void checkFilmId(Long id) {
        if (filmStorage.findById(id).isEmpty()) {
            String errorMessage = String.format("Фильм с id %d не найден.", id);
            throw new NotFoundException(errorMessage);
        }
    }
}
