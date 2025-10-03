package ru.yandex.practicum.filmorate.validation.validator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class ReleaseDateValidatorTest {
    private final ReleaseDateValidator validator = new ReleaseDateValidator();

    @Test
    void mustReturnTrueIfDateIsValid() {
        assertTrue(validator.isValid(LocalDate.of(1895, 12, 28), null),
                "Должен вернуть true если дата 28.12.1895.");
        assertTrue(validator.isValid(LocalDate.of(2025, 10, 4), null),
                "Должен вернуть true если дата идет после 28.12.1895.");
    }

    @Test
    void mustReturnFalseIfDateIsInvalid() {
        assertFalse(validator.isValid(LocalDate.of(1, 1, 1), null),
                "Должен вернуть false если дата предшествует 28.12.1895.");
        assertFalse(validator.isValid(LocalDate.of(1895, 12, 27), null),
                "Должен вернуть false если дата предшествует 28.12.1895.");
    }

    @Test
    void mustReturnTrueIfDateIsNull() {
        assertTrue(validator.isValid(null, null),
                "Должен вернуть true если дата не указана.");
    }
}