package ru.yandex.practicum.filmorate.dal.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findAll();

    List<Film> findPopular(long count);

    Optional<Film> findById(Long id);

    Film create(Film film);

    Film update(Film newFilm);

    void addLike(Long id, Long userId);

    boolean deleteLike(Long id, Long userId);

    boolean existsById(Long id);

    boolean mpaExistsById(Long id);

    boolean genreExistsById(List<Long> genreIds);

    void clear();
}
