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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
class FilmDbStorageTest {
    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private UserDbStorage userStorage;

    private Mpa mpa;
    private Genre genre;
    private Film film;
    private User user;

    @BeforeEach
    void setup() {
        mpa = Mpa.builder()
                .id(1L)
                .name("G")
                .build();

        genre = Genre.builder()
                .id(1L)
                .name("Комедия")
                .build();

        film = Film.builder()
                .name("TestMovie")
                .description("TestDescription")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(200)
                .mpa(mpa)
                .build();

        film.setGenres(List.of(genre));

        user = User.builder()
                .name("TestName")
                .login("TestLogin")
                .email("test@test.com")
                .birthday(LocalDate.of(2000, 12, 31))
                .build();
    }

    @Test
    void mustCreateFilm() {
        Film created = filmStorage.create(film);

        assertNotNull(created.getId(), "ID должен быть присвоен");
        assertEquals("TestMovie", created.getName());
        assertEquals("TestDescription", created.getDescription());
        assertEquals(LocalDate.of(1895, 12, 28), created.getReleaseDate());
        assertEquals(200, created.getDuration());
        assertEquals(1L, created.getMpa().getId());
    }

    @Test
    void mustUpdateFilmAndGenres() {
        Film saved = filmStorage.create(film);
        Genre genre2 = Genre.builder()
                .id(2L)
                .name("Драма")
                .build();

        saved.setName("Updated");
        saved.setDescription("Updated description");
        saved.setReleaseDate(LocalDate.of(2010, 5, 5));
        saved.setDuration(150);

        saved.setMpa(Mpa.builder().id(2L).name("PG").build());

        saved.setGenres(List.of(genre2));

        filmStorage.update(saved);

        Film fromDb = filmStorage.findById(saved.getId())
                .orElseThrow(() -> new AssertionError("Фильм должен существовать после update"));

        assertEquals("Updated", fromDb.getName());
        assertEquals("Updated description", fromDb.getDescription());
        assertEquals(LocalDate.of(2010, 5, 5), fromDb.getReleaseDate());
        assertEquals(150, fromDb.getDuration());

        assertNotNull(fromDb.getMpa());
        assertEquals(2L, fromDb.getMpa().getId());
        assertEquals("PG", fromDb.getMpa().getName());

        assertEquals(1, fromDb.getGenres().size());
        assertEquals("Драма", fromDb.getGenres().iterator().next().getName(), "Жанр должен быть заменен");
    }

    @Test
    void mustRemoveGenresIfUpdatedWithEmptyList() {
        Film saved = filmStorage.create(film);

        saved.setGenres(Collections.emptyList());
        filmStorage.update(saved);

        Film fromDb = filmStorage.findById(saved.getId())
                .orElseThrow();

        assertTrue(fromDb.getGenres().isEmpty(), "Жанры должны быть удалены");
    }

    @Test
    void mustFindAllFilms() {
        Film saved = filmStorage.create(film);

        List<Film> films = filmStorage.findAll();

        assertEquals(1, films.size(), "Должен быть один фильм");
        Film f = films.get(0);

        assertNotNull(f.getId(), "ID должен быть присвоен");
        assertEquals("TestMovie", f.getName());
        assertEquals("TestDescription", f.getDescription());
        assertEquals(LocalDate.of(1895, 12, 28), f.getReleaseDate());
        assertEquals(200, f.getDuration());

        assertNotNull(f.getMpa(), "MPA должен быть загружен");
        assertEquals(1L, f.getMpa().getId());
        assertEquals("G", f.getMpa().getName());

        assertNotNull(f.getGenres(), "Жанры должны быть загружены");
        assertEquals(1, f.getGenres().size());
        assertEquals("Комедия", f.getGenres().iterator().next().getName());
    }

    @Test
    void mustFindFilmById() {
        Film saved = filmStorage.create(film);

        Optional<Film> foundOpt = filmStorage.findById(saved.getId());
        assertTrue(foundOpt.isPresent(), "Фильм должен быть найден");

        Film found = foundOpt.get();

        assertEquals(saved.getId(), found.getId());
        assertEquals("TestMovie", found.getName());
        assertEquals("TestDescription", found.getDescription());
        assertEquals(LocalDate.of(1895, 12, 28), found.getReleaseDate());
        assertEquals(200, found.getDuration());

        assertNotNull(found.getMpa(), "MPA должен быть загружен");
        assertEquals(1L, found.getMpa().getId());
        assertEquals("G", found.getMpa().getName());

        assertNotNull(found.getGenres(), "Жанры должны быть загружены");
        assertEquals(1, found.getGenres().size());
        assertEquals("Комедия", found.getGenres().iterator().next().getName());
    }

    @Test
    void mustReturnEmptyIfFilmNotFound() {
        Optional<Film> result = filmStorage.findById(999L);
        assertTrue(result.isEmpty(), "Должен вернуться пустой Optional");
    }

    @Test
    void testAddLike() {
        user = userStorage.create(user);
        film = filmStorage.create(film);

        filmStorage.addLike(film.getId(), user.getId());

        Film updated = filmStorage.findById(film.getId()).orElseThrow();

        assertTrue(updated.getLikes().contains(user.getId()), "Лайк должен быть добавлен");
        assertEquals(1, updated.getLikes().size(), "Должен быть один лайк");
    }

    @Test
    void testDeleteLike() {
        user = userStorage.create(user);
        film = filmStorage.create(film);
        filmStorage.addLike(film.getId(), user.getId());

        boolean deleted = filmStorage.deleteLike(film.getId(), user.getId());
        Film updated = filmStorage.findById(film.getId()).orElseThrow();

        assertTrue(deleted, "Метод должен вернуть true при удалении");
        assertFalse(updated.getLikes().contains(user.getId()), "Лайк должен быть удален");
    }

    @Test
    void testDeleteLikeWhenNotExists() {
        user = userStorage.create(user);
        film = filmStorage.create(film);
        boolean deleted = filmStorage.deleteLike(film.getId(), user.getId());

        assertFalse(deleted, "Метод должен вернуть false если лайка не было");
    }

    @Test
    void testFindPopular() {
        User user1 = userStorage.create(user.toBuilder().email("1@test.com").login("u1").build());
        User user2 = userStorage.create(user.toBuilder().email("2@test.com").login("u2").build());
        User user3 = userStorage.create(user.toBuilder().email("3@test.com").login("u3").build());

        Film filmA = filmStorage.create(film.toBuilder().name("FilmA").build());
        Film filmB = filmStorage.create(film.toBuilder().name("FilmB").build());
        Film filmC = filmStorage.create(film.toBuilder().name("FilmC").build());

        filmStorage.addLike(filmA.getId(), user1.getId());
        filmStorage.addLike(filmA.getId(), user2.getId());
        filmStorage.addLike(filmB.getId(), user3.getId());

        List<Film> popular = filmStorage.findPopular(3);

        assertEquals(3, popular.size(), "Должны вернуться три фильма");

        assertEquals(filmA.getId(), popular.get(0).getId(), "Фильм с наибольшим количеством лайков должен быть первым");
        assertEquals(filmB.getId(), popular.get(1).getId(), "Фильм со вторым количеством лайков должен быть вторым");
        assertEquals(filmC.getId(), popular.get(2).getId(), "Фильм без лайков должен идти последним");

        assertEquals(2, popular.get(0).getLikes().size(), "У первого фильма должно быть 2 лайка");
        assertEquals(1, popular.get(1).getLikes().size(), "У второго фильма должен быть 1 лайк");
        assertEquals(0, popular.get(2).getLikes().size(), "У третьего фильма лайков быть не должно");
    }
}