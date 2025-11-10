package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = {"id"})
public class Genre {
    Long id;
    String name;
}
