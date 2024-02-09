package ru.java.practicum.filmorate.controller; // Sprint 12 - file 1 - review controller

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.java.practicum.filmorate.model.Review;
import ru.java.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteReview(@PathVariable long id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review findReviewById(@PathVariable long id) {
        return reviewService.findReviewById(id);
    }

    @GetMapping
    public List<Review> getReviewsOfFilm(@RequestParam(defaultValue = "0") long filmId,
                                         @RequestParam(defaultValue = "10") int count) {
        return reviewService.getReviewsOfFilm(filmId, count);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping(value = "/{id}/dislike/{userId}")
    public void addDislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLike(@PathVariable long reviewId, @PathVariable long userId) {
        reviewService.deleteLike(reviewId, userId);
    }

    @DeleteMapping(value = "/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable long reviewId, @PathVariable long userId) {
        reviewService.deleteDislike(reviewId, userId);
    }
}
