package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    public List<Film> findAll();

    public Film create(Film film);

    public Film update(Film newFilm);

    public void clear();
}
