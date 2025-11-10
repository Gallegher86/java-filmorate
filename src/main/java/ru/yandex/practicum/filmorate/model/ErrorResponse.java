package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
public class ErrorResponse {
    private String errorMessage;
    private int errorCode;
    private List<String> details;
}
