package ru.yandex.practicum.filmorate.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Documented;
import ru.yandex.practicum.filmorate.validation.validator.ReleaseDateValidator;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
@Documented
public @interface ValidReleaseDate {
    String message() default "Дата выпуска фильма не может быть раньше 28.12.1895.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
