package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.storage.GenreStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@Service
public class GenreServiceImplementation implements GenreService {
    private final GenreStorage genreStorage;

    public GenreServiceImplementation(@Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Override
    public List<Genre> findAll() {
        List<Genre> genres = genreStorage.findAll();
        log.info("Список жанров выдан.");
        return genres;
    }

    @Override
    public Genre findById(Long id) {
        Genre genre = genreStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Жанр с id %d не найден.", id)));

        log.info("Жанр с id {} выдан.", id);
        return genre;
    }
}
