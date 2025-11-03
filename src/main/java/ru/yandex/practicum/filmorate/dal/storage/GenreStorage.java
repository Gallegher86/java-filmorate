package ru.yandex.practicum.filmorate.dal.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    List<Genre> findAll();

    Optional<Genre> findByGenreId(Long id);

    List<Genre> findByFilmId(Long id);
}
