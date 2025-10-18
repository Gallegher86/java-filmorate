package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> findAll();

    Film findById(Long id);

    Film create(Film newFilm);

    Film update(Film updatedFilm);

    Film addLike(Long id, Long userId);

    Film deleteLike(Long id, Long userId);

    List<Film> getPopularFilms(long count);

    void checkFilmId(Long id);
}
