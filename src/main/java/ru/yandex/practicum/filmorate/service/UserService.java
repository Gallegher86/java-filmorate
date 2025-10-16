package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User create(User user);

    User update(User newUser);

    User addFriend (Long id, Long FriendId);

    User removeFriend(Long id, Long friendId);

    List<User> findFriends(Long id);

    void clear();
}
