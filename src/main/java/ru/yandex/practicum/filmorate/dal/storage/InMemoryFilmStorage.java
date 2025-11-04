package ru.yandex.practicum.filmorate.dal.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.MethodNotImplementedException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Film> findPopular(long count) {
        throw new MethodNotImplementedException("Метод не реализован.");
    }

    @Override
    public Optional<Film> findById(Long id) {
        Film film = films.get(id);
        return Optional.ofNullable(film);
    }

    @Override
    public Film create(Film film) {
        long id = generateNextId();
        log.trace("Сгенерирован новый id для фильма {}", id);
        film.setId(id);
        update(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        log.trace("Фильм {} с id {} сохранен.", film.getName(), film.getId());
        return film;
    }

    @Override
    public void addLike(Long id, Long userId) {
        throw new MethodNotImplementedException("Метод не реализован.");
    }

    @Override
    public boolean deleteLike(Long id, Long userId) {
        throw new MethodNotImplementedException("Метод не реализован.");
    }

    @Override
    public boolean existsById(Long id) {
        return films.containsKey(id);
    }

    @Override
    public boolean mpaExistsById(Long id) {
        throw new MethodNotImplementedException("Метод не реализован.");
    }

    @Override
    public boolean genreExistsById(List<Long> genreIds) {
        throw new MethodNotImplementedException("Метод не реализован.");
    }

    @Override
    public void clear() {
        films.clear();
        idCounter = 1L;
        log.trace("FilmStorage очищен.");
    }

    private Long generateNextId() {
        return idCounter++;
    }
}
