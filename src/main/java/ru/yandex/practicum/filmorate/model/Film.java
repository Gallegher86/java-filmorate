package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validation.annotation.ValidReleaseDate;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class Film {
    private Long id;
    @NotBlank (message = "Название фильма не может быть пустым.")
    private String name;
    @Length(max = 200, message = "Описание фильма должно включать не более двухсот символов.")
    private String description;
    @ValidReleaseDate
    private LocalDate releaseDate;
    @Positive (message = "Продолжительность фильма должна быть положительной.")
    private int duration;
}

