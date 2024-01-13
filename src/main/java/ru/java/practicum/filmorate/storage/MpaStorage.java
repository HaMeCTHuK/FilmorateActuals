package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.model.Genre;
import ru.java.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage  {

    List<Mpa> getAll();

    Mpa get(Long id);

}
