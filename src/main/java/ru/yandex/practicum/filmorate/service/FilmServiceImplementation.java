package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.storage.FilmStorage;
import ru.yandex.practicum.filmorate.model.Genre;

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
        Long mpaId = newFilm.getMpa().getId();
        List<Long> genreIds = newFilm.getGenres().stream().map(Genre::getId).toList();

        if (mpaId == null) {
            throw new MpaNotFoundException("MPA id должен быть указан.");
        } else {
            checkMpa(mpaId);
        }

        if (!genreIds.isEmpty()) {
            checkGenres(genreIds);
        }

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
        if (filmStorage.deleteLike(id, userId)) {
            log.info("Пользователь с userId {} удалил лайк фильму с id {}.", userId, id);
        } else {
            log.info("Пользователь с userId {} не ставил лайк фильму с id {}.", userId, id);
        }
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

    private void checkMpa(Long id) {
        if (!filmStorage.mpaExistsById(id)) {
            String errorMessage = String.format("Рейтинг mpa c id %d не найден.", id);
            throw new MpaNotFoundException(errorMessage);
        }
    }

    private void checkGenres(List<Long> genreIds) {
        if (!filmStorage.genreExistsById(genreIds)) {
            throw new GenreNotFoundException("Жанр не найден.");
        }
    }
}
