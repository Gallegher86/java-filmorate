package ru.yandex.practicum.filmorate.dal.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = parseMpa(rs.getLong("mpa_id"), rs.getString("mpa_name"));
        Set<Genre> genres = parseGenres(rs.getString("genres"));
        Set<Long> likes = parseLikes(rs.getString("likes"));

        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .genres(genres)
                .likes(likes)
                .build();
    }

    private Mpa parseMpa(Long id, String name) {
        if (id == 0) return null;
        return Mpa.builder().id(id).name(name).build();
    }

    private Set<Genre> parseGenres(String genresStr) {
        Set<Genre> genres = new HashSet<>();
        if (genresStr != null && !genresStr.isEmpty()) {
            for (String g : genresStr.split(",")) {
                String[] parts = g.split(":");
                if (parts.length == 2) {
                    genres.add(Genre.builder().id(Long.parseLong(parts[0])).name(parts[1]).build());
                }
            }
        }
        return genres;
    }

    private Set<Long> parseLikes(String likesStr) {
        Set<Long> likes = new HashSet<>();
        if (likesStr != null && !likesStr.isEmpty()) {
            for (String s : likesStr.split(",")) {
                likes.add(Long.parseLong(s));
            }
        }
        return likes;
    }
}
