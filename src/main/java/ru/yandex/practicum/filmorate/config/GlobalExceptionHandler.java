package ru.yandex.practicum.filmorate.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;

import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException ex) {
        log.warn("Ресурс не найден: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("errorMessage", ex.getMessage());
        body.put("errorCode", HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidArgumentException(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        log.warn("Валидация не пройдена ({}): {}.", ex.getClass().getSimpleName(), errors);

        body.put("errorMessage", "Выявлены следующие ошибки валидации:");
        body.put("details", errors);
        body.put("errorCode", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> resourceNotFound(NoResourceFoundException ex) {
        String pathErrorMessage = String.format("Ресурс по пути %s не найден.", ex.getResourcePath());
        log.warn(pathErrorMessage);

        Map<String, Object> body = new HashMap<>();
        body.put("errorMessage", "Ресурс по указанному пути не найден.");
        body.put("errorCode", HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUncaughtException(Exception ex) {
        log.error("Необработанное исключение: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        Map<String, Object> body = new HashMap<>();
        body.put("errorMessage", "Произошла ошибка на сервере.");
        body.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}