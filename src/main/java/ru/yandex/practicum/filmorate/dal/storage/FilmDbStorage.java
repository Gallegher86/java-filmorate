package ru.yandex.practicum.filmorate.dal.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.util.*;

@Slf4j
@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?," +
            "duration = ?, mpa_id = ? WHERE id = ?";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_FILM_GENRE_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String ADD_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String DELETE_ALL_FILMS_QUERY = "DELETE FROM films";
    private static final String EXISTS_BY_ID_QUERY = "SELECT EXISTS(SELECT 1 FROM films WHERE id = ?)";
    private static final String FIND_POPULAR_QUERY =
            "SELECT f.*, COUNT(l.user_id) AS likes_count " +
                    "FROM films AS f " +
                    "LEFT JOIN likes AS l ON f.id = l.film_id " +
                    "GROUP BY f.id " +
                    "ORDER BY likes_count DESC " +
                    "LIMIT ?;";

    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, GenreStorage genreStorage, MpaStorage mpaStorage) {
        super(jdbc, mapper);
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        log.debug("Фильмы выгружены из базы данных.");

        for (Film film : films) {
            loadMpa(film);
            loadGenres(film);
        }

        log.trace("Список фильмов в базе данных готов к выдаче.");
        return films;
    }

    @Override
    public List<Film> findPopular(long count) {
        List<Film> films = findMany(FIND_POPULAR_QUERY, count);

        log.debug("{} популярных фильмов выгружены из базы данных.", count);

        for (Film film : films) {
            loadMpa(film);
            loadGenres(film);
        }

        log.trace("Список {} популярных фильмов в базе данных готов к выдаче.", count);
        return films;
    }

    @Override
    public Optional<Film> findById(Long id) {
        Optional<Film> filmOpt = findOne(FIND_BY_ID_QUERY, id);

        if (filmOpt.isEmpty()) {
            log.debug("findById. Фильм с id {} отсутствует в базе данных.", id);
            return filmOpt;
        }

        Film film = filmOpt.get();
        loadMpa(film);
        loadGenres(film);

        log.trace("Фильм с id {} готов к выдаче.", id);
        return Optional.of(film);
    }

    @Override
    public Film create(Film film) {
        checkMpa(film);
        checkGenre(film);

        Long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        saveGenres(film);
        log.trace("Фильм {} с id {} сохранен в БД.", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        Long id = film.getId();
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                id
        );
        log.debug("update. Данные фильма с id {} в films БД обновлены.", id);

        deleteFilmGenres(id);

        if (!film.getGenres().isEmpty()) {
            saveGenres(film);
        }
        log.debug("update. Жанры фильма с id {} в film_genre БД обновлены.", id);

        log.trace("Фильм {} с id {} обновлен.", film.getName(), film.getId());
        return film;
    }

    @Override
    public boolean existsById(Long id) {
        return existsById(EXISTS_BY_ID_QUERY, id);
    }

    @Override
    public void addLike(Long id, Long userId) {
        update(ADD_LIKE_QUERY, id, userId);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        update(DELETE_LIKE_QUERY, id, userId);
    }

    @Override
    public void clear() {
        execute(DELETE_ALL_FILMS_QUERY);
    }

    private void saveGenres(Film film) {
        if (film.getGenres().isEmpty()) {
            return;
        }

        Long id = film.getId();

        for (Genre genre : film.getGenres()) {
            update(INSERT_FILM_GENRE_QUERY, id, genre.getId());
        }
        log.debug("Жанры фильма c id {} помещены в film_genre БД.", id);
    }

    private void deleteFilmGenres(Long filmId) {
        delete(DELETE_FILM_GENRE_QUERY, filmId);
    }

    private void loadMpa(Film film) {
        Long id = film.getId();

        film.setMpa(mpaStorage.findByFilmId(id)
                .orElse(null));
        log.debug("Рейтинг mpa фильма с id {} выгружен из базы данных.", id);
    }

    private void loadGenres(Film film) {
        Long id = film.getId();

        List<Genre> genres = genreStorage.findByFilmId(id);
        film.addGenres(genres);
        log.debug("Жанры фильма с id {} выгружены из базы данных.", id);
    }

    private void checkMpa(Film film) {
        Mpa mpa = film.getMpa();
        List<Mpa> validMpa = mpaStorage.findAll();

        if (mpa != null && !validMpa.contains(mpa)) {
            throw new MpaNotFoundException("Рейтинг MPA не найден в списке: " + mpa +
                    ". Допустимые значения: " + validMpa);
        }
    }

    private void checkGenre(Film film) {
        List<Genre> genres = film.getGenres();
        List<Genre> dbGenres = genreStorage.findAll();

        if (!genres.isEmpty() && !dbGenres.containsAll(genres)) {
            throw new GenreNotFoundException("Жанр не найден в списке. Допустимые значения:" + dbGenres);
        }
    }
}
