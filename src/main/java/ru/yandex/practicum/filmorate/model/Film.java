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
    private final Set<Genre> genres = new HashSet<>();
    private final Set<Long> likes = new HashSet<>();

    public void addLikeId(Long id) {
        likes.add(id);
    }

    public void removeLikeId(Long id) {
        likes.remove(id);
    }

    public Set<Long> getLikes() {
        return new HashSet<>(likes);
    }

    public List<Genre> getGenres() {
        return genres.stream()
                .sorted(Comparator.comparingLong(Genre::getId))
                .toList();
    }

    public void addGenres(List<Genre> filmGenres) {
        genres.addAll(filmGenres);
    }
}

