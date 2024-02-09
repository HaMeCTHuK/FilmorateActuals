package ru.java.practicum.filmorate.model; // add-reviews- new file 3

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
@Data
public class Review {
    private long reviewId;
    @Length(max = 200, message = "Максимальная длина отзыва — 200 символов")
    @NotEmpty
    private final String content;
    @NotNull
    private final Boolean isPositive;
    @NotNull
    private final Long userId;
    @NotNull
    private final Long filmId;
    private long useful;
}