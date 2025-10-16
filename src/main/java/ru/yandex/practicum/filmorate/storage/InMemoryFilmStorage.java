package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
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
        films.put(id, film);
        log.info("Фильм {} с id {} помещен  в коллекцию.", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film save(Film film) {
        films.put(film.getId(), film);
        log.trace("Фильм {} с id {} сохранен.", film.getName(), film.getId());
        return film;
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
