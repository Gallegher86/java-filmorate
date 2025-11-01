package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = {"id"})
public class Mpa {
    Integer id;
    String name;
}
