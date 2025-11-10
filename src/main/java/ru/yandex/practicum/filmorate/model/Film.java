package ru.yandex.practicum.filmorate.model;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validation.annotation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.*;

@Builder (toBuilder = true)
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
    private Mpa mpa;
    private Set<Genre> genres;
    private Set<Long> likes;

    public List<Genre> getGenres() {
        return genres == null ? List.of() : genres.stream()
                .sorted(Comparator.comparingLong(Genre::getId))
                .toList();
    }

    public void setGenres(List<Genre> filmGenres) {
        if (genres == null) genres = new HashSet<>();
        genres.clear();
        genres.addAll(filmGenres);
    }

    public HashSet<Long> getLikes() {
        return likes == null ? new HashSet<>() : new HashSet<>(likes);
    }

    public void setLikes(List<Long> likesId) {
        if (likes == null) likes = new HashSet<>();
        likes.addAll(likesId);
    }
}

