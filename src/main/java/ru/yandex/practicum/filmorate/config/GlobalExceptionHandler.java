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
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        log.warn("Ресурс не найден: {}", ex.getMessage());

        ErrorResponse body = ErrorResponse.builder()
                .errorMessage(ex.getMessage())
                .errorCode(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArgumentException(MethodArgumentNotValidException ex) {
        String errorMessage = "Выявлены ошибки валидации.";

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        log.warn("Валидация не пройдена ({}): {}.", ex.getClass().getSimpleName(), errors);

        ErrorResponse body = ErrorResponse.builder()
                .errorMessage(errorMessage)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .details(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn(ex.getMessage());

        ErrorResponse body = ErrorResponse.builder()
                .errorMessage(ex.getMessage())
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleWrongPath(NoResourceFoundException ex) {
        String errorMessage = "Ресурс по указанному пути не найден.";
        String logMessage = String.format("Получен запрос на несуществующий путь %s.", ex.getResourcePath());
        log.warn(logMessage);

        ErrorResponse body = ErrorResponse.builder()
                .errorMessage(errorMessage)
                .errorCode(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleWrongRequestMethod(HttpRequestMethodNotSupportedException ex) {
        String errorMessage = String.format("Метод %s не поддерживается.", ex.getMethod());
        String logMessage = String.format("Получен запрос с нереализованным методом %s.", ex.getMethod());
        log.warn(logMessage);

        ErrorResponse body = ErrorResponse.builder()
                .errorMessage(errorMessage)
                .errorCode(HttpStatus.METHOD_NOT_ALLOWED.value())
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String value = String.valueOf(ex.getValue());
        String errorMessage = String.format("Неверный формат параметра запроса '%s': '%s'. Ожидается число.", name, value);
        log.warn(errorMessage);

        ErrorResponse body = ErrorResponse.builder()
                .errorMessage(errorMessage)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MpaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMpaNotFoundException(MpaNotFoundException ex) {
        log.warn(ex.getMessage());

        ErrorResponse body = ErrorResponse.builder()
                .errorMessage(ex.getMessage())
                .errorCode(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(GenreNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGenreNotFoundException(GenreNotFoundException ex) {
        log.warn(ex.getMessage());

        ErrorResponse body = ErrorResponse.builder()
                .errorMessage(ex.getMessage())
                .errorCode(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUncaughtException(Exception ex) {
        String errorMessage = "Произошла ошибка на сервере.";
        log.error("Необработанное исключение: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        ErrorResponse body = ErrorResponse.builder()
                .errorMessage(errorMessage)
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}