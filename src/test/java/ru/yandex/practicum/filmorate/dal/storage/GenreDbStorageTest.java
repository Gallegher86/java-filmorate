package ru.yandex.practicum.filmorate.dal.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
class GenreDbStorageTest {
    @Autowired
    private GenreDbStorage genreStorage;

    @Autowired
    private FilmDbStorage filmStorage;

    Genre genre;
    Mpa mpa;
    Film film;


    @BeforeEach
    void setup() {
        genre = Genre.builder()
                .id(1L)
                .name("Комедия")
                .build();

        mpa = Mpa.builder()
                .id(1L)
                .name("G")
                .build();

        film = Film.builder()
                .name("TestMovie")
                .description("TestDescription")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(200)
                .mpa(mpa)
                .build();

        film.setGenres(List.of(genre));
    }

    @Test
    void testFindAll() {
        List<Genre> genres = genreStorage.findAll();
        assertNotNull(genres);
        assertFalse(genres.isEmpty(), "Список жанров не должен быть пустым");
        assertTrue(genres.stream().anyMatch(g -> g.getName().equals("Комедия")));
    }

    @Test
    void testFindByGenreId() {
        Optional<Genre> genreOpt = genreStorage.findByGenreId(1L);
        assertTrue(genreOpt.isPresent());
        assertEquals("Комедия", genreOpt.get().getName());

        Optional<Genre> missing = genreStorage.findByGenreId(999L);
        assertTrue(missing.isEmpty());
    }

    @Test
    void testFindByFilmId() {
        film = filmStorage.create(film);
        List<Genre> filmGenres = genreStorage.findByFilmId(film.getId());
        assertNotNull(filmGenres);
        assertEquals(1, filmGenres.size());
        assertEquals("Комедия", filmGenres.get(0).getName());
    }

    @Test
    void testExistsById() {
        assertTrue(genreStorage.existsById(List.of(1L)));
        assertFalse(genreStorage.existsById(List.of(999L)));
        assertFalse(genreStorage.existsById(List.of(1L, 999L)));
    }

}