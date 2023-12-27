package ru.java.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;

@Data
@EqualsAndHashCode(of = {"id"}) //для сравнения используем только поле id
@SuperBuilder    //аннотация которая позволяет создавать классы билдер для классов наследников
@NoArgsConstructor
public abstract class BaseUnit {
    private long id;
    public abstract HashSet<Long> getClassSet();

}
