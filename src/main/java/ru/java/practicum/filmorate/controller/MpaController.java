package ru.java.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.java.practicum.filmorate.model.Mpa;
import ru.java.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public List<Mpa> getAllMpa() {
        final List<Mpa> mpaRating = mpaService.getAllMpa();
        log.info("Получаем список рейтинга МРА");
        return mpaRating;
    }

    @GetMapping
    @RequestMapping("/{id}")
    public Mpa getMpa(@PathVariable long id) {
      Mpa mpaRating = mpaService.getMpa(id);
      log.info("Получаем рейтинг МРА по айди");
      return mpaRating;
    }


}
