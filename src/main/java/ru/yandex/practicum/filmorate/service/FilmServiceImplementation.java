package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImplementation implements FilmService {
    private final FilmStorage filmStorage;

    @Override
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film newFilm) {
        Long id = newFilm.getId();

        if (filmStorage.findById(id).isEmpty()) {
            String errorMessage = String.format("Фильм с id %d не найден.", id);
            throw new NotFoundException(errorMessage);
        }

        Film film = filmStorage.save(newFilm);
        log.info("Обновленный фильм {} с id {} помещен  в коллекцию.", film.getName(), film.getId());
        return film;
    }

    public void clear() {
        filmStorage.clear();
    }
}
