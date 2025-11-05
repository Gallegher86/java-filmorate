package ru.yandex.practicum.filmorate.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String MESSAGE = "errorMessage";
    private static final String CODE = "errorCode";
    private static final String DETAILS = "details";

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException ex) {
        log.warn("Ресурс не найден: {}", ex.getMessage());

        Map<String, Object> body = makeBody(ex.getMessage(), HttpStatus.NOT_FOUND.value(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidArgumentException(MethodArgumentNotValidException ex) {
        String errorMessage = "Выявлены следующие ошибки валидации:";

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        log.warn("Валидация не пройдена ({}): {}.", ex.getClass().getSimpleName(), errors);

        Map<String, Object> body = makeBody(errorMessage, HttpStatus.BAD_REQUEST.value(), errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn(ex.getMessage());

        Map<String, Object> body = makeBody(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleWrongPath(NoResourceFoundException ex) {
        String errorMessage = "Ресурс по указанному пути не найден.";
        String logMessage = String.format("Получен запрос на несуществующий путь %s.", ex.getResourcePath());
        log.warn(logMessage);

        Map<String, Object> body = makeBody(errorMessage, HttpStatus.NOT_FOUND.value(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleWrongRequestMethod(HttpRequestMethodNotSupportedException ex) {
        String errorMessage = String.format("Метод %s не поддерживается.", ex.getMethod());
        String logMessage = String.format("Получен запрос с нереализованным методом %s.", ex.getMethod());
        log.warn(logMessage);

        Map<String, Object> body = makeBody(errorMessage, HttpStatus.METHOD_NOT_ALLOWED.value(), null);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String value = String.valueOf(ex.getValue());
        String errorMessage = String.format("Неверный формат параметра запроса '%s': '%s'. Ожидается число.", name, value);
        log.warn(errorMessage);

        Map<String, Object> body = makeBody(errorMessage, HttpStatus.BAD_REQUEST.value(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MpaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMpaNotFoundException(MpaNotFoundException ex) {
        log.warn(ex.getMessage());

        Map<String, Object> body = makeBody(ex.getMessage(), HttpStatus.NOT_FOUND.value(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(GenreNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleGenreNotFoundException(GenreNotFoundException ex) {
        log.warn(ex.getMessage());

        Map<String, Object> body = makeBody(ex.getMessage(), HttpStatus.NOT_FOUND.value(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUncaughtException(Exception ex) {
        String errorMessage = "Произошла ошибка на сервере.";
        log.error("Необработанное исключение: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        Map<String, Object> body = makeBody(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private Map<String, Object> makeBody(String message, int status, List<String> details) {
        Map<String, Object> body = new HashMap<>();
        body.put(MESSAGE, message);

        if (details != null) {
            body.put(DETAILS, details);
        }

        body.put(CODE, status);
        return body;
    }
}