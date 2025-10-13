package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private FilmService filmService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Film film = Film.builder()
            .name("TestMovie")
            .description("TestDescription")
            .releaseDate(LocalDate.of(1895, 12, 28))
            .duration(200)
            .build();

    @BeforeEach
    public void clear() {
        filmService.clear();
    }

    @Test
    public void mustCreateFilmAndReturn201() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
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