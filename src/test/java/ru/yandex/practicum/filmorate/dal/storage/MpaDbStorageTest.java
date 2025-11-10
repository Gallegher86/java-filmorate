package ru.yandex.practicum.filmorate.dal.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
class MpaDbStorageTest {
    @Autowired
    private MpaDbStorage mpaStorage;

    @Autowired
    private FilmDbStorage filmStorage;

    Mpa mpa;
    Film film;

    @BeforeEach
    void setup() {
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
    }

    @Test
    void testFindAll() {
        List<Mpa> mpas = mpaStorage.findAll();
        assertNotNull(mpas);
        assertTrue(mpas.size() >= 5, "Должно быть как минимум 5 MPA");
        assertTrue(mpas.stream().anyMatch(m -> m.getName().equals("G")));
    }

    @Test
    void testFindById() {
        Optional<Mpa> mpaOpt = mpaStorage.findById(1L);
        assertTrue(mpaOpt.isPresent());
        assertEquals("G", mpaOpt.get().getName());

        Optional<Mpa> missing = mpaStorage.findById(999L);
        assertTrue(missing.isEmpty(), "Не существующий id должен возвращать empty");
    }

    @Test
    void testFindByFilmId() {
        film = filmStorage.create(film);
        Optional<Mpa> mpaOpt = mpaStorage.findByFilmId(film.getId());
        assertTrue(mpaOpt.isPresent());
        assertEquals(film.getMpa().getName(), mpaOpt.get().getName());
    }

    @Test
    void testExistsById() {
        assertTrue(mpaStorage.existsById(1L));
        assertFalse(mpaStorage.existsById(999L));
    }
}