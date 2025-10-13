package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    public List<Film> findAll();

    public Optional<Film> findById(Long id);

    public Film create(Film film);

    public Film save(Film newFilm);

    public void clear();
}
