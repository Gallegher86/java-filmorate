package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.storage.MpaStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Service
public class MpaServiceImplementation implements MpaService {
    private final MpaStorage mpaStorage;

    public MpaServiceImplementation(@Qualifier("mpaDbStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Mpa> findAll() {
        List<Mpa> mpas = mpaStorage.findAll();
        log.info("Список рейтингов mpa выдан.");
        return mpas;
    }

    @Override
    public Mpa findById(Long id) {
        Mpa mpa = mpaStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Рейтинг mpa с id %d не найден.", id)));

        log.info("Рейтинг с id {} выдан.", id);
        return mpa;
    }
}