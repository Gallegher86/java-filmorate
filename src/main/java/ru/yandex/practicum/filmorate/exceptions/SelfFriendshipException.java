package ru.yandex.practicum.filmorate.exceptions;

public class SelfFriendshipException extends RuntimeException {
    public SelfFriendshipException(String message) {
        super(message);
    }
}
