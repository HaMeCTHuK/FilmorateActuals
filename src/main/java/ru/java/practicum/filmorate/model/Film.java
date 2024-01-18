package ru.java.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


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
    @Min(value = 1, message = "Не может быть <= 0")
    private int duration;
    private int rating;
    @NotNull
    private Mpa mpa;
    @NotNull
    private List<Genre> genres = new ArrayList<>();
    @JsonIgnore
    private Long likes = 0L;

    public Film(
            String name,
            String description,
            LocalDate releaseDate,
            int duration,
            int rating,
            Mpa mpa,
            Long likes) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rating = rating;
        this.mpa = mpa;
        this.likes = likes;
    }
}