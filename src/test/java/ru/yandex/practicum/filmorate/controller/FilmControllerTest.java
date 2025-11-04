package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.storage.FilmStorage;
import ru.yandex.practicum.filmorate.dal.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=password",
        "spring.sql.init.mode=always"
})
class FilmControllerTest {
    @Autowired
    @Qualifier("filmDbStorage")
    private FilmStorage filmStorage;

    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Mpa mpa = Mpa.builder()
            .id(1L)
            .name("G")
            .build();

    private Genre genre = Genre.builder()
            .id(1L)
            .name("Комедия")
            .build();

    private Film film = Film.builder()
            .name("TestMovie")
            .description("TestDescription")
            .releaseDate(LocalDate.of(1895, 12, 28))
            .duration(200)
            .mpa(mpa)
            .build();

    private User user = User.builder()
            .name("TestName")
            .login("TestLogin")
            .email("test@test.com")
            .birthday(LocalDate.of(2000, 12, 31))
            .build();

    @BeforeEach
    void addGenre() {
        film.addGenres(List.of(genre));
    }

    @Test
    public void mustCreateFilmAndReturn201() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name").value("TestMovie"))
                .andExpect(jsonPath("$.description").value("TestDescription"))
                .andExpect(jsonPath("$.releaseDate").value("1895-12-28"))
                .andExpect(jsonPath("$.duration").value("200"));
    }

    @Test
    public void mustUpdateFilmAndReturn200() throws Exception {
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)));

        film = Film.builder()
                .id(1L)
                .name("UpdatedName")
                .description("UpdatedDescription")
                .releaseDate(LocalDate.of(2010, 12, 31))
                .duration(120)
                .mpa(mpa)
                .build();

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.description").value("UpdatedDescription"))
                .andExpect(jsonPath("$.releaseDate").value("2010-12-31"))
                .andExpect(jsonPath("$.duration").value("120"));
    }

    @Test
    public void mustFindFilmsAndReturn200() throws Exception {
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("TestMovie"))
                .andExpect(jsonPath("$[0].description").value("TestDescription"))
                .andExpect(jsonPath("$[0].releaseDate").value("1895-12-28"))
                .andExpect(jsonPath("$[0].duration").value("200"));
    }

    @Test
    public void mustFindFilmByIdAndReturn200() throws Exception {
        System.out.println(film.getReleaseDate());
        filmStorage.create(film);
        System.out.println(film.getReleaseDate());

        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("TestMovie"))
                .andExpect(jsonPath("$.description").value("TestDescription"))
                .andExpect(jsonPath("$.releaseDate").value("1895-12-28"))
                .andExpect(jsonPath("$.duration").value("200"));
    }

    @Test
    public void mustReturn404IdfFilmNotFoundById() throws Exception {
        mockMvc.perform(get("/films/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Фильм с id 999 не найден."));
    }

    @Test
    public void mustReturn404IfFilmNotFoundOnUpdate() throws Exception {
        film = film.toBuilder()
                .id(999L)
                .build();

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Фильм с id 999 не найден."));
    }

    @Test
    public void mustReturn400IfValidationErrorsFound() throws Exception {
        Film emptyNameFilm = film.toBuilder()
                .name("")
                .build();
        checkValidation(emptyNameFilm, "name: Название фильма не может быть пустым.");

        Film longDescriptionFilm = film.toBuilder()
                .description("a".repeat(201))
                .build();
        checkValidation(longDescriptionFilm,
                "description: Описание фильма должно включать не более двухсот символов.");

        Film earlyReleaseDateFilm = film.toBuilder()
                .releaseDate(LocalDate.of(1895, 12, 27))
                .build();
        checkValidation(earlyReleaseDateFilm,
                "releaseDate: Дата выпуска фильма не может быть раньше 28.12.1895.");

        Film zeroDurationFilm = film.toBuilder()
                .duration(0)
                .build();
        checkValidation(zeroDurationFilm,
                "duration: Продолжительность фильма должна быть положительной.");

        Film negativeDuratiionFilm = film.toBuilder()
                .duration(-999)
                .build();

        checkValidation(negativeDuratiionFilm,
                "duration: Продолжительность фильма должна быть положительной.");
    }

    @Test
    public void mustAddLikeAndReturn200() throws Exception {
        filmStorage.create(film);
        userStorage.create(user);

        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void mustDeleteLikeAndReturn200() throws Exception {
        filmStorage.create(film);
        userStorage.create(user);
        mockMvc.perform(put("/films/1/like/1"));

        mockMvc.perform(delete("/films/1/like/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void mustReturn404IfAddOrDeleteLikeToFilmWhichNotExist() throws Exception {
        userStorage.create(user);

        mockMvc.perform(put("/films/999/like/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Фильм с id 999 не найден."));

        mockMvc.perform(delete("/films/999/like/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Фильм с id 999 не найден."));
    }

    @Test
    public void mustReturn404IfAddOrDeleteLikeFromUserWhichNotExist() throws Exception {
        filmStorage.create(film);

        mockMvc.perform(put("/films/1/like/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Пользователь с id 999 не найден."));

        mockMvc.perform(delete("/films/1/like/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Пользователь с id 999 не найден."));
    }

    @Test
    public void mustGetPopularFilmsAndReturn200() throws Exception {
        filmStorage.create(film);
        filmStorage.create(film);
        userStorage.create(user);
        mockMvc.perform(put("/films/2/like/1"));

        mockMvc.perform(get("/films/popular?count=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    public void mustGet10FilmsIfCountIsNotSet() throws Exception {
        for (int i = 0; i < 11; i++) {
            filmStorage.create(film.toBuilder().build());
        }

        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(10)));
    }

    @Test
    public void mustReturn400IfCountIsNegative() throws Exception {
        mockMvc.perform(get("/films/popular?count=-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Параметр {count} не может быть отрицательным."));
    }

    @Test
    public void mustReturn404IfMpaIsWrong() throws Exception {
        Mpa wrongMpa = Mpa.builder()
                .id(999L)
                .name("WRONG_MPA")
                .build();
        film.setMpa(wrongMpa);

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Рейтинг mpa c id 999 не найден."));
    }

    @Test
    public void mustReturn404IfGenreIsWrong() throws Exception {
        Genre wrongGenre = Genre.builder()
                .id(999L)
                .name("WRONG_GENRE")
                .build();
        film.addGenres(List.of(wrongGenre));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Жанр не найден."));
    }

    private void checkValidation(Film film, String expectedMessage) throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details")
                        .value(expectedMessage));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details")
                        .value(expectedMessage));
    }
}