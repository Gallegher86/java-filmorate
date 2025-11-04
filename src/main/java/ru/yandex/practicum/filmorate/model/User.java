package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import lombok.*;

import java.time.LocalDate;

@Builder (toBuilder = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class User {
    private Long id;
    @NotBlank(message = "Электронный адрес не может быть пустым.")
    @Email(message = "Электронный адрес должен содержать символ @ и быть корректным.")
    private String email;
    @NotBlank(message = "Логин не должен быть пустым.")
    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелов.")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;

    public String getName() {
        return (name == null || name.isBlank()) ? login : name;
    }
}
