package ru.yandex.practicum.filmorate.dal.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseDbStorage<Mpa> implements MpaStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa WHERE id = ?";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT m.id, m.name FROM films AS f " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.id WHERE f.id = ?";
    private static final String EXISTS_BY_ID_QUERY = "SELECT EXISTS(SELECT 1 FROM mpa WHERE id = ?)";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Mpa> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Mpa> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Optional<Mpa> findByFilmId(Long id) {
        return findOne(FIND_BY_FILM_ID_QUERY, id);
    }

    @Override
    public boolean existsById(Long id) {
        return exists(EXISTS_BY_ID_QUERY, id);
    }
}
