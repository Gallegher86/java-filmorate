package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;

public class SelfFriendshipException extends RuntimeException {
    @Getter
    private Long id;

    public SelfFriendshipException(String message) {
        super(message);
    }

    public SelfFriendshipException(String message, Long id) {
        super(message);
        this.id = id;
    }
}
