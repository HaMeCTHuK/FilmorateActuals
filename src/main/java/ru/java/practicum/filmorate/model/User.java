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
public class User extends BaseUnit {
    @Email
    @NotNull
    private String email;
    @NotBlank(message = "Не может быть пустым")
    @NotNull
    private String login;
    private String name;
    @PastOrPresent(message = "не может быть в будущем")
    private LocalDate birthday;

}
