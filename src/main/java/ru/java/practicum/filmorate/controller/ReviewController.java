package ru.java.practicum.filmorate.controller; // add-reviews- new file 1

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.java.practicum.filmorate.model.Review;
import ru.java.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {

        this.reviewService = reviewService;
    }

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        log.info("Пытаемся создать отзыв: {}", review);
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("Пытаемся обновить отзыв: {}", review);
        return reviewService.update(review);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteReview(@PathVariable long id) {
        log.info("Пытаемся удалить отзыв с id: {}", id);
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review findReviewById(@PathVariable long id) {
        log.info("Пытаемся найти отзыв по id: {}", id);
        return reviewService.findReviewById(id);
    }

    @GetMapping
    public List<Review> getReviewsOfFilm(@RequestParam(defaultValue = "0") long filmId,
                                         @RequestParam(defaultValue = "10") int count) {
        log.info("Получаем отзывы для фильма с id: {} (количество: {})", filmId, count);
        return reviewService.getReviewsOfFilm(filmId, count);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Пытаемся добавить лайк к отзыву с id: {} от пользователя с id: {}", id, userId);
        reviewService.addLike(id, userId);
    }

    @PutMapping(value = "/{id}/dislike/{userId}")
    public void addDislike(@PathVariable long id, @PathVariable long userId) {
        log.info("Пытаемся добавить дизлайк к отзыву с id: {} от пользователя с id: {}", id, userId);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLike(@PathVariable long reviewId, @PathVariable long userId) {
        log.info("Пытаемся удалить лайк отзыва с id: {} от пользователя с id: {}", reviewId, userId);
        reviewService.deleteLike(reviewId, userId);
    }

    @DeleteMapping(value = "/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable long reviewId, @PathVariable long userId) {
        log.info("Пытаемся удалить дизлайк отзыва с id: {} от пользователя с id: {}", reviewId, userId);
        reviewService.deleteDislike(reviewId, userId);
    }
}
