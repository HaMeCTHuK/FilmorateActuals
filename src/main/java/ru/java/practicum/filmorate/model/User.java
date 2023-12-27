package ru.java.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;

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
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private HashSet<Long> friends = new HashSet<>(); // для другов

    public boolean addFriend(Long userId) {
        return friends.add(userId);
    }

    public boolean deleteFriend(Long userId) {
        return friends.remove(userId);
    }

    @Override
    public HashSet<Long> getClassSet() {
        return new HashSet<>(getFriends());
    }

}
