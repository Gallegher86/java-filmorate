package ru.yandex.practicum.filmorate.dal.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_BY_GENRE_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT g.id, g.name FROM film_genre AS fg " +
            "JOIN genres AS g ON fg.genre_id = g.id WHERE fg.film_id = ? ORDER BY fg.genre_id";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Genre> findByGenreId(Long id) {
        return findOne(FIND_BY_GENRE_ID_QUERY, id);
    }

    @Override
    public List<Genre> findByFilmId(Long id) {
        return findMany(FIND_BY_FILM_ID_QUERY, id);
    }

    @Override
    public boolean existsById(List<Long> genreIds) {
        String placeholders = String.join(", ", Collections.nCopies(genreIds.size(), "?"));
        String query = "SELECT COUNT(*) FROM genres WHERE id IN (" + placeholders + ")";

        Integer count = jdbc.queryForObject(
                query,
                Integer.class,
                genreIds.toArray());

        return count != null && count == genreIds.size();
    }
}
