package ru.java.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.java.practicum.filmorate.model.Director;
import ru.java.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;


    //Создание режиссёра
    @PostMapping
    public Director createDirector(@RequestBody @Valid Director director) {
        log.info("Пытаемся добавить режиссера : {}", director);
        return directorService.create(director);
    }

    //Изменение режиссёра
    @PutMapping()
    public Director updateDirector(@RequestBody @Valid Director director) {
        log.info("Пытаемся обновить данные режиссера : {}", director);
        return directorService.update(director);
    }

    //Список всех режиссёров
    @GetMapping
    public List<Director> getAllDirector() {
        List<Director> allDirectors = directorService.getAll();
        log.info("Текущее количество режиссеров: {}", allDirectors.size());
        return allDirectors;
    }

    //Получение режиссёра по id
    @GetMapping("/{id}")
    public Director getDirector(@RequestBody @PathVariable Long id) {
        log.info("Получаем режиссера по id: {}", id);
        return directorService.getData(id);
    }

    //Удаление режиссёра по id
    @DeleteMapping("/{id}")
    public void deleteDirector(@RequestBody @PathVariable Long id) {
        log.info("Удаляем режиссера по id: {}", id);
        directorService.delete(id);
    }
}
