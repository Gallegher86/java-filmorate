package ru.yandex.practicum.filmorate.dal.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.MethodNotImplementedException;
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
        throw new MethodNotImplementedException("Метод не реализован.");
    }

    @Override
    public User create(User user) {
        Long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday())
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
    public boolean existsById(Long id) {
        return existsById(EXISTS_BY_ID_QUERY, id);
    }

    @Override
    public void clear() {
        throw new MethodNotImplementedException("Метод не реализован.");
    }
}
