package ru.yandex.practicum.filmorate.dal.storage;

import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();

    Optional<User> findById(Long id);

    List<User> findFriends(Long id);

    List<User> findCommonFriends(Long id, Long otherId);

    User create(User user);

    User update(User user);

    void addFriend(Long userId, Long friendId, FriendStatus status);

    void updateFriend(Long userId, Long friendId, FriendStatus status);

    void removeFriend(Long userId, Long friendId);

    boolean existsById(Long id);

    boolean friendshipExists(Long userId, Long friendId);

    void clear();
}
