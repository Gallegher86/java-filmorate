package ru.yandex.practicum.filmorate.dal.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
class UserDbStorageTest {
    @Autowired
    private UserDbStorage userStorage;

    private User user;

    @BeforeEach
    void setup() {
           user = User.builder()
                .name("TestName")
                .login("TestLogin")
                .email("test@test.com")
                .birthday(LocalDate.of(2000, 12, 31))
                .build();
    }

    @Test
    void testCreateUser() {
        User created = userStorage.create(user);

        assertNotNull(created.getId(), "ID должен быть присвоен");
        assertEquals("TestName", created.getName());
        assertEquals("TestLogin", created.getLogin());
        assertEquals("test@test.com", created.getEmail());
        assertEquals(LocalDate.of(2000, 12, 31), created.getBirthday());

        User fromDb = userStorage.findById(created.getId())
                .orElseThrow(() -> new AssertionError("Пользователь должен быть найден в БД"));

        assertEquals(created.getId(), fromDb.getId());
        assertEquals("TestName", fromDb.getName());
        assertEquals("TestLogin", fromDb.getLogin());
        assertEquals("test@test.com", fromDb.getEmail());
    }

    @Test
    void testUpdateUser() {
        User created = userStorage.create(user);

        created.setName("Updated Name");
        created.setLogin("UpdatedLogin");
        created.setEmail("updated@test.com");
        created.setBirthday(LocalDate.of(1999, 1, 1));

        userStorage.update(created);

        User updated = userStorage.findById(created.getId())
                .orElseThrow(() -> new AssertionError("Пользователь должен существовать в БД"));

        assertEquals(created.getId(), updated.getId());
        assertEquals("Updated Name", updated.getName());
        assertEquals("UpdatedLogin", updated.getLogin());
        assertEquals("updated@test.com", updated.getEmail());
        assertEquals(LocalDate.of(1999, 1, 1), updated.getBirthday());
    }

    @Test
    void testFindAllUsers() {
        User user1 = userStorage.create(user.toBuilder().build());
        User user2 = userStorage.create(user.toBuilder().login("Login2").email("test2@test.com").build());

        List<User> allUsers = userStorage.findAll();

        assertEquals(2, allUsers.size(), "Должны вернуться все пользователи");
        assertTrue(allUsers.stream().anyMatch(u -> u.getId().equals(user1.getId())));
        assertTrue(allUsers.stream().anyMatch(u -> u.getId().equals(user2.getId())));
    }

    @Test
    void testFindById() {
        User created = userStorage.create(user);

        Optional<User> found = userStorage.findById(created.getId());

        assertTrue(found.isPresent(), "Пользователь должен быть найден");
        assertEquals(created.getId(), found.get().getId());
        assertEquals(created.getLogin(), found.get().getLogin());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<User> found = userStorage.findById(999L);
        assertTrue(found.isEmpty(), "Пользователь с несуществующим id должен отсутствовать");
    }

    @Test
    void testFindFriendsNonMutual() {
        User user1 = userStorage.create(user.toBuilder().build());
        User user2 = userStorage.create(user.toBuilder().login("Login2").email("test2@test.com").build());

        userStorage.addFriend(user1.getId(), user2.getId(), FriendStatus.PENDING);

        List<User> friendsOfUser1 = userStorage.findFriends(user1.getId());
        assertEquals(1, friendsOfUser1.size());
        assertEquals(user2.getId(), friendsOfUser1.get(0).getId());

        List<User> friendsOfUser2 = userStorage.findFriends(user2.getId());
        assertTrue(friendsOfUser2.isEmpty());
    }

    @Test
    void testFindCommonFriends() {
        User user1 = userStorage.create(user.toBuilder().build());
        User user2 = userStorage.create(user.toBuilder().login("Login2").email("test2@test.com").build());
        User user3 = userStorage.create(user.toBuilder().login("Login3").email("test3@test.com").build());
        User user4 = userStorage.create(user.toBuilder().login("Login4").email("test4@test.com").build());

        userStorage.addFriend(user1.getId(), user3.getId(), FriendStatus.PENDING);
        userStorage.addFriend(user1.getId(), user4.getId(), FriendStatus.PENDING);

        userStorage.addFriend(user2.getId(), user3.getId(), FriendStatus.PENDING);

        List<User> common = userStorage.findCommonFriends(user1.getId(), user2.getId());

        assertEquals(1, common.size());
        assertEquals(user3.getId(), common.get(0).getId());
    }
}