package ru.java.practicum.filmorate.storage;

import ru.java.practicum.filmorate.model.Director;
import ru.java.practicum.filmorate.model.Film;

import java.util.List;


public interface DirectorStorage extends AbstractStorage<Director> {


    List<Film> getSortedDirectorListByYear(Long directorId);

    List<Film> getSortedDirectorListByLikes(Long directorId);
}
