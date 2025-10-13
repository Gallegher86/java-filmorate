package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User user = User.builder()
            .name("TestName")
            .login("TestLogin")
            .email("test@test.com")
            .birthday(LocalDate.of(2000, 12, 31))
            .build();

    @BeforeEach
    public void clear() {
        userService.clear();
    }

    @Test
    public void mustCreateUserAndReturn201() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("TestName"))
                .andExpect(jsonPath("$.login").value("TestLogin"))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.birthday").value("2000-12-31"));
    }

    @Test
    public void mustUpdateUserAndReturn200() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        user = User.builder()
                .id(1L)
                .name("UpdatedName")
                .login("UpdatedLogin")
                .email("updatedMail@test.com")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.login").value("UpdatedLogin"))
                .andExpect(jsonPath("$.email").value("updatedMail@test.com"))
                .andExpect(jsonPath("$.birthday").value("1980-01-01"));
    }

    @Test
    public void mustFindFilmsAndReturn200() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("TestName"))
                .andExpect(jsonPath("$[0].login").value("TestLogin"))
                .andExpect(jsonPath("$[0].email").value("test@test.com"))
                .andExpect(jsonPath("$[0].birthday").value("2000-12-31"));
    }

    @Test
    public void mustReturn404IfUserNotFoundOnUpdate() throws Exception {
        user = user.toBuilder()
                .id(999L)
                .build();

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Пользователь с id 999 не найден."));
    }

    @Test
    public void mustReturnLoginIfNameIsEmpty() throws Exception {
        User noNameUser = user.toBuilder()
                .name("")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noNameUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("TestLogin"));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("TestLogin"));
    }

    @Test
    public void mustReturn400IfValidationErrorsFound() throws Exception {
        User noEmailUser = user.toBuilder()
                .email("")
                .build();
        checkValidation(noEmailUser,
                "email: Электронный адрес не может быть пустым.");

        User wrongEmailUser = user.toBuilder()
                .email("это-неправильный?эмейл@")
                .build();
        checkValidation(wrongEmailUser,
                "email: Электронный адрес должен содержать символ @ и быть корректным.");

        User emptyLoginUser = user.toBuilder()
                .login("")
                .build();
        checkValidation(emptyLoginUser,
                "login: Логин не должен быть пустым.");

        User loginWithSpacesUser = user.toBuilder()
                .login("wrong login")
                .build();
        checkValidation(loginWithSpacesUser,
                "login: Логин не должен содержать пробелов.");

        User futureUser = user.toBuilder()
                .birthday(LocalDate.of(40000, 12, 31))
                .build();
        checkValidation(futureUser,
                "birthday: Дата рождения не может быть в будущем.");
    }

    private void checkValidation(User user, String expectedMessage) throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details")
                        .value(expectedMessage));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details")
                        .value(expectedMessage));
    }
}