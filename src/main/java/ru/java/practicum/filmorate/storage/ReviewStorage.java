package ru.java.practicum.filmorate.storage; // Sprint 12 - file 6 controller

import ru.java.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    void deleteReview(long id);

    Review findReviewById(long id);

    List<Review> getReviewsOfFilm(long filmId, int count);

    void addLike(long reviewId, long userId);

    void addDislike(long reviewId, long userId);

    void deleteLike(long reviewId, long userId);

    void deleteDislike(long reviewId, long userId);
}
