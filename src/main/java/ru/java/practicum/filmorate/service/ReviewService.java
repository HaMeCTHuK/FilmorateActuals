package ru.java.practicum.filmorate.service; // add-reviews- new file 4

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.java.practicum.filmorate.model.Review;
import ru.java.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage) {

        this.reviewStorage = reviewStorage;
    }

    public Review create(Review review) {

        return reviewStorage.create(review);
    }

    public Review update(Review review) {

        return reviewStorage.update(review);
    }

    public void deleteReview(long id) {

        reviewStorage.deleteReview(id);
    }

    public Review findReviewById(long id) {

        return reviewStorage.findReviewById(id);
    }

    public List<Review> getReviewsOfFilm(long filmId, int count) {

        return reviewStorage.getReviewsOfFilm(filmId, count);
    }

    public void addLike(long reviewId, long userId) {

        reviewStorage.addLike(reviewId, userId);
    }

    public void addDislike(long reviewId, long userId) {

        reviewStorage.addDislike(reviewId, userId);
    }

    public void deleteLike(long reviewId, long userId) {

        reviewStorage.deleteLike(reviewId, userId);
    }

    public void deleteDislike(long reviewId, long userId) {

        reviewStorage.deleteDislike(reviewId, userId);
    }
}
