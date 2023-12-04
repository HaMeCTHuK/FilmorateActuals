package ru.java.practicum.filmorate.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import javax.validation.constraints.*;
import java.time.LocalDate;


@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Film extends BaseUnit {

    @NotNull(message = "Не может быть null")
    @NotBlank(message = "Не может быть пустым")
    private String name;
    @Size(max = 200, message = "Не более 200 символов")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    //@PositiveOrZero (message = "Не может быть <= 0")  (оставил специально для себя)
    @Min(value = 1, message = "Не может быть <= 0")
    private int duration;

}