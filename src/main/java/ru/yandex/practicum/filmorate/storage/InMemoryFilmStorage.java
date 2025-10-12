package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Long, Film> films = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    };

    @Override
    public Film create(Film film) {
        long id = generateNextId();
        log.trace("Сгенерирован новый id для фильма {}", id);
        film.setId(id);
        films.put(id, film);
        log.info("Фильм {} с id {} помещен  в коллекцию.", film.getName(), film.getId());
        return film;
    };

    @Override
    public Film update(Film newFilm) {
        Long id = newFilm.getId();

        Film oldFilm = films.get(id);
        if (Objects.isNull(oldFilm)) {
            String errorMessage = String.format("Фильм с id %d не найден.", id);
            throw new NotFoundException(errorMessage);
        }

        films.put(id, newFilm);
        log.info("Обновленный фильм {} с id {} помещен  в коллекцию.", newFilm.getName(), newFilm.getId());
        return newFilm;
    };

    public void clear() {
        films.clear();
        idCounter = 1L;
    }

    private Long generateNextId() {
        return idCounter++;
    }
}
