package ru.yandex.practicum.filmorate.dal.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE id = ?";
    private static final String EXISTS_BY_ID_QUERY = "SELECT EXISTS(SELECT 1 FROM users WHERE id = ?)";
    private static final String FRIENDSHIP_EXISTS_QUERY = "SELECT EXISTS(SELECT 1 FROM friends " +
            "WHERE user_id = ? AND friend_id = ?)";
    private static final String INSERT_FRIENDSHIP_QUERY = "INSERT INTO friends (user_id, friend_id, status) " +
            "VALUES (?, ?, ?)";
    private static final String UPDATE_FRIENDSHIP_QUERY = "UPDATE friends SET status = ? " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String REMOVE_FRIEND_QUERY = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_FRIENDS_BY_USER_ID_QUERY = "SELECT friend_id FROM friends WHERE user_id = ?";
    private static final String FIND_FRIENDS_QUERY =
            "SELECT u.* " +
                    "FROM users u " +
                    "JOIN friends f " +
                    "ON u.id = f.friend_id " +
                    "WHERE f.user_id = ?";
    private static final String FIND_COMMON_FRIENDS_QUERY =
            "SELECT u.* " +
                    "FROM users u " +
                    "JOIN friends f1 ON u.id = f1.friend_id " +
                    "JOIN friends f2 ON u.id = f2.friend_id " +
                    "WHERE f1.user_id = ? " +
                    "AND f2.user_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAll() {
        List<User> users = findMany(FIND_ALL_QUERY);
        log.trace("Пользователи выгружены из базы данных.");
        return users;
    }

    @Override
    public Optional<User> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public List<User> findFriends(Long id) {
        List<User> friends = findMany(FIND_FRIENDS_QUERY, id);
        log.trace("Список друзей пользователя с id {} выгружен из базы данных.", id);
        return friends;
    }

    @Override
    public List<User> findCommonFriends(Long id, Long otherId) {
        List<User> commonFriends = findMany(FIND_COMMON_FRIENDS_QUERY, id, otherId);
        log.trace("Список общих друзей пользователей с id {} и otherId {} выгружен из базы данных.", id, otherId);
        return commonFriends;
    }

    @Override
    public User create(User user) {
        Long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        log.trace("Пользователь {} с id {} сохранен в БД.", user.getLogin(), user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        Long id = user.getId();
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                id
        );
        log.trace("Пользователь {} с id {} обновлен.", user.getLogin(), id);
        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId, FriendStatus status) {
        update(INSERT_FRIENDSHIP_QUERY, userId, friendId, status.name());
        log.trace("Создана дружба userId {} с friendId {}, статус {}.", userId, friendId, status.name());
    }

    @Override
    public void updateFriend(Long userId, Long friendId, FriendStatus status) {
        update(UPDATE_FRIENDSHIP_QUERY, status.name(), userId, friendId);
        log.trace("Обновлена дружба userId {} с friendId {}, статус {}.", userId, friendId, status.name());
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        delete(REMOVE_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public boolean existsById(Long id) {
        return exists(EXISTS_BY_ID_QUERY, id);
    }

    @Override
    public boolean friendshipExists(Long userId, Long friendId) {
        return exists(FRIENDSHIP_EXISTS_QUERY, userId, friendId);
    }

    private void loadFriends(User user) {
        Long id = user.getId();

        List<Long> friends = jdbc.query(
                FIND_FRIENDS_BY_USER_ID_QUERY,
                (rs, rowNum) -> rs.getLong("friend_id"),
                id
        );

        user.setFriends(friends);
    }
}
