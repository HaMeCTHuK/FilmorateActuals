package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.Mpa;
import ru.java.practicum.filmorate.model.Review;
import ru.java.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private FilmDbStorage filmStorage;
    private UserDbStorage userStorage;
    private ReviewDbStorage reviewDbStorage;

    @BeforeEach
    void init() {
        GenreDbStorage genreStorage = new GenreDbStorage(jdbcTemplate);
        DirectorDbStorage directorDbStorage = new DirectorDbStorage(jdbcTemplate);
        LikesDbStorage likeStorage = new LikesDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, likeStorage, directorDbStorage, genreStorage);
        userStorage = new UserDbStorage(jdbcTemplate);
        reviewDbStorage = new ReviewDbStorage(jdbcTemplate,filmStorage, userStorage);
    }

    private static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void shouldCreateReview() {

        User user = User.builder()
                .login("Iris")
                .name("Melissa")
                .email("catcat@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userStorage.create(user);

        Film film = Film.builder()
                .name("Форрест Гамп")
                .description("Жизнь как коробка конфет")
                .releaseDate(LocalDate.of(1981, 12, 6))
                .duration(192)
                .rating(1)
                .mpa(new Mpa())
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .likes(10L)
                .build();

        film.getMpa().setId(1);
        filmStorage.create(film);

        Review review = Review.builder()
                .content("Лучший фильм!")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        reviewDbStorage.create(review);

        assertTrue(reviewDbStorage.getReviewsOfFilm(film.getId(), 1).contains(review));
    }

    @Test
    public void shouldNotPassContentValidation() {

        User user = User.builder()
                .login("Ivory")
                .name("Melissa")
                .email("Ivory@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userStorage.create(user);

        Film film = Film.builder()
                .name("Форрест Гамп")
                .description("Жизнь как коробка конфет")
                .releaseDate(LocalDate.of(1981, 12, 6))
                .duration(192)
                .rating(1)
                .mpa(new Mpa())
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .likes(10L)
                .build();

        film.getMpa().setId(1);
        filmStorage.create(film);

        Review review = Review.builder()
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();

        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldNotPassIsPositiveValidation() {

        User user = User.builder()
                .login("Rina")
                .name("Melissa")
                .email("Rina@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userStorage.create(user);

        Film film = Film.builder()
                .name("Форрест Гамп")
                .description("Жизнь как коробка конфет")
                .releaseDate(LocalDate.of(1981, 12, 6))
                .duration(192)
                .rating(1)
                .mpa(new Mpa())
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .likes(10L)
                .build();

        film.getMpa().setId(1);
        filmStorage.create(film);

        Review review = Review.builder()
                .content("Лучший фильм!")
                .userId(user.getId())
                .filmId(film.getId())
                .build();

        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldNotPassUserIdValidation() {

        User user = User.builder()
                .login("Lime")
                .name("Melissa")
                .email("Lime@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userStorage.create(user);

        Film film = Film.builder()
                .name("Форрест Гамп")
                .description("Жизнь как коробка конфет")
                .releaseDate(LocalDate.of(1981, 12, 6))
                .duration(192)
                .rating(1)
                .mpa(new Mpa())
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .likes(10L)
                .build();

        film.getMpa().setId(1);
        filmStorage.create(film);

        Review review = Review.builder()
                .content("Лучший фильм!")
                .isPositive(true)
                .filmId(film.getId())
                .build();

        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldNotPassFilmIdValidation() {

        User user = User.builder()
                .login("Lemon")
                .name("Melissa")
                .email("Lemon@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userStorage.create(user);

        Film film = Film.builder()
                .name("Форрест Гамп")
                .description("Жизнь как коробка конфет")
                .releaseDate(LocalDate.of(1981, 12, 6))
                .duration(192)
                .rating(1)
                .mpa(new Mpa())
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .likes(10L)
                .build();

        film.getMpa().setId(1);
        filmStorage.create(film);

        Review review = Review.builder()
                .content("Лучший фильм!")
                .isPositive(true)
                .userId(user.getId())
                .build();

        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldUpdateReview() {

        User user = User.builder()
                .login("Melon")
                .name("Melissa")
                .email("Melon@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userStorage.create(user);

        Film film = Film.builder()
                .name("Форрест Гамп")
                .description("Жизнь как коробка конфет")
                .releaseDate(LocalDate.of(1981, 12, 6))
                .duration(192)
                .rating(1)
                .mpa(new Mpa())
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .likes(10L)
                .build();

        film.getMpa().setId(1);
        filmStorage.create(film);

        Review review = Review.builder()
                .content("Не очень")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        reviewDbStorage.create(review);

        Review reviewUpdate = Review.builder()
                .reviewId(review.getReviewId())
                .content("Мне понравился")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        reviewDbStorage.update(reviewUpdate);

        assertEquals(reviewUpdate, reviewDbStorage.findReviewById(review.getReviewId()));
    }

    @Test
    public void shouldDeleteReview() {

        Film film = Film.builder()
                .name("Форрест Гамп")
                .description("Жизнь как коробка конфет")
                .releaseDate(LocalDate.of(1981, 12, 6))
                .duration(192)
                .rating(1)
                .mpa(new Mpa())
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .likes(10L)
                .build();

        film.getMpa().setId(1);
        filmStorage.create(film);

        User user = User.builder()
                .login("Melody")
                .name("Melissa")
                .email("Melody@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userStorage.create(user);

        Review review = Review.builder()
                .content("Не очень")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        reviewDbStorage.create(review);
        reviewDbStorage.deleteReview(review.getReviewId());

        assertFalse(reviewDbStorage.getReviewsOfFilm(film.getId(), 1).contains(review));
    }

    @Test
    public void shouldFindReviewById() {

        Film film = Film.builder()
                .name("Форрест Гамп")
                .description("Жизнь как коробка конфет")
                .releaseDate(LocalDate.of(1981, 12, 6))
                .duration(192)
                .rating(1)
                .mpa(new Mpa())
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .likes(10L)
                .build();

        film.getMpa().setId(1);
        filmStorage.create(film);

        User user = User.builder()
                .login("Peony")
                .name("Melissa")
                .email("Peony@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userStorage.create(user);

        Review review = Review.builder()
                .content("Не очень")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        reviewDbStorage.create(review);

        assertEquals(review, reviewDbStorage.findReviewById(review.getReviewId()));
    }

    @Test
    public void shouldGetReviewsOfFilm() {

        Film film = Film.builder()
                .name("Форрест Гамп")
                .description("Жизнь как коробка конфет")
                .releaseDate(LocalDate.of(1981, 12, 6))
                .duration(192)
                .rating(1)
                .mpa(new Mpa())
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .likes(10L)
                .build();

        film.getMpa().setId(1);
        filmStorage.create(film);

        User user = User.builder()
                .login("Chestnut")
                .name("Melissa")
                .email("Chestnut@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userStorage.create(user);

        Review review = Review.builder()
                .content("Не очень")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        reviewDbStorage.create(review);

        Review review2 = Review.builder()
                .reviewId(review.getReviewId())
                .content("Мне понравился")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        reviewDbStorage.create(review2);

        assertEquals(List.of(review, review2), reviewDbStorage.getReviewsOfFilm(film.getId(), 2));
    }

    @Test
    public void shouldAddLike() {

        Film film = Film.builder()
                .name("Форрест Гамп")
                .description("Жизнь как коробка конфет")
                .releaseDate(LocalDate.of(1981, 12, 6))
                .duration(192)
                .rating(1)
                .mpa(new Mpa())
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .likes(10L)
                .build();

        film.getMpa().setId(1);
        filmStorage.create(film);

        User user = User.builder()
                .login("Nat")
                .name("Melissa")
                .email("Nat@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userStorage.create(user);

        Review review = Review.builder()
                .content("Неплохо")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        reviewDbStorage.create(review);
        reviewDbStorage.addLike(review.getReviewId(), user.getId());

        assertEquals(1, reviewDbStorage.findReviewById(review.getReviewId()).getUseful());
    }

    @Test
    public void shouldAddDislike() {

        Film film = Film.builder()
                .name("Форрест Гамп")
                .description("Жизнь как коробка конфет")
                .releaseDate(LocalDate.of(1981, 12, 6))
                .duration(192)
                .rating(1)
                .mpa(new Mpa())
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .likes(10L)
                .build();

        film.getMpa().setId(1);
        filmStorage.create(film);

        User user = User.builder()
                .login("Cotton")
                .name("Melissa")
                .email("Cotton@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userStorage.create(user);

        Review review = Review.builder()
                .content("Не очень")
                .isPositive(false)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        reviewDbStorage.create(review);
        reviewDbStorage.addDislike(review.getReviewId(), user.getId());

        assertEquals(-1, reviewDbStorage.findReviewById(review.getReviewId()).getUseful());
    }

    @Test
    public void shouldDeleteLike() {

        Film film = Film.builder()
                .name("Форрест Гамп")
                .description("Жизнь как коробка конфет")
                .releaseDate(LocalDate.of(1981, 12, 6))
                .duration(192)
                .rating(1)
                .mpa(new Mpa())
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .likes(10L)
                .build();

        film.getMpa().setId(1);
        filmStorage.create(film);

        User user = User.builder()
                .login("Wool")
                .name("Melissa")
                .email("Wool@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userStorage.create(user);

        Review review = Review.builder()
                .content("Неплохо")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        reviewDbStorage.create(review);
        reviewDbStorage.addLike(review.getReviewId(), user.getId());
        reviewDbStorage.deleteLike(review.getReviewId(), user.getId());

        assertEquals(0, reviewDbStorage.findReviewById(review.getReviewId()).getUseful());
    }

    @Test
    public void shouldDeleteDislike() {

        Film film = Film.builder()
                .name("Форрест Гамп")
                .description("Жизнь как коробка конфет")
                .releaseDate(LocalDate.of(1981, 12, 6))
                .duration(192)
                .rating(1)
                .mpa(new Mpa())
                .genres(Collections.emptyList())
                .directors(Collections.emptyList())
                .likes(10L)
                .build();

        film.getMpa().setId(1);
        filmStorage.create(film);

        User user = User.builder()
                .login("Leaf")
                .name("Melissa")
                .email("Leaf@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userStorage.create(user);

        Review review = Review.builder()
                .content("Не очень")
                .isPositive(false)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        reviewDbStorage.create(review);
        reviewDbStorage.addDislike(review.getReviewId(), user.getId());
        reviewDbStorage.deleteDislike(review.getReviewId(), user.getId());

        assertEquals(0, reviewDbStorage.findReviewById(review.getReviewId()).getUseful());
    }
}
