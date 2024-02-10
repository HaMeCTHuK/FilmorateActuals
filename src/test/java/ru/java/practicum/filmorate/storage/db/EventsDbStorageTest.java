package ru.java.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.java.practicum.filmorate.event.EventOperation;
import ru.java.practicum.filmorate.event.EventType;
import ru.java.practicum.filmorate.model.Film;
import ru.java.practicum.filmorate.model.Mpa;
import ru.java.practicum.filmorate.model.Review;
import ru.java.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventsDbStorageTest {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    private DirectorDbStorage directorDbStorage;
    private LikesDbStorage likeStorage;
    private FilmDbStorage filmStorage;
    private UserDbStorage userStorage;
    private GenreDbStorage genreStorage;
    private EventDbStorage eventStorage;
    private FriendsDbStorage friendsStorage;
    private ReviewDbStorage reviewDbStorage;

    @BeforeEach
    void init() {
        genreStorage = new GenreDbStorage(jdbcTemplate);
        directorDbStorage = new DirectorDbStorage(jdbcTemplate, genreStorage);
        likeStorage = new LikesDbStorage(jdbcTemplate, genreStorage, directorDbStorage);
        filmStorage = new FilmDbStorage(jdbcTemplate, likeStorage, directorDbStorage, genreStorage);
        userStorage = new UserDbStorage(jdbcTemplate);
        eventStorage = new EventDbStorage(jdbcTemplate);
        friendsStorage = new FriendsDbStorage(jdbcTemplate);
        reviewDbStorage = new ReviewDbStorage(jdbcTemplate, filmStorage, userStorage);
    }

    @Test
    public void insertAddRemoveLikeEventInDb() {
        Film newFilm1 = new Film(
                "testFilmr1",
                "description1",
                LocalDate.of(1999,2,24),
                40,
                1,
                new Mpa(),
                10L);
        newFilm1.getMpa().setId(5);

        User newUser1 = new User(
                "test1@email.ru",
                "test1",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));

        // Записываем фильмы в БД
        filmStorage.create(newFilm1);
        // Записываем пользователей в БД
        userStorage.create(newUser1);
        // Добавляем событие в БД
        likeStorage.addLike(newFilm1.getId(), newUser1.getId());

        eventStorage.addEvent(newUser1.getId(), newFilm1.getId(), EventType.LIKE, EventOperation.ADD);
        assertThat(eventStorage.getUserEvents(newUser1.getId()).get(0).getOperation().toString()).isEqualTo("ADD");

        eventStorage.addEvent(newUser1.getId(), newFilm1.getId(), EventType.LIKE, EventOperation.REMOVE);
        assertThat(eventStorage.getUserEvents(newUser1.getId()).get(1).getOperation().toString()).isEqualTo("REMOVE");
    }

    @Test
    public void insertAddRemoveFriendsEventInDb() {
        User newUser1 = new User(
                "test1@email.ru",
                "test1",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));

        User newUser2 = new User(
                "test2@email.ru",
                "test2",
                "Ivan Petrov",
                LocalDate.of(1990, 1, 1));

        // Записываем пользователей в БД
        userStorage.create(newUser1);
        userStorage.create(newUser2);

        friendsStorage.addFriend(newUser1.getId(), newUser2.getId());

        eventStorage.addEvent(newUser1.getId(), newUser2.getId(), EventType.FRIEND, EventOperation.ADD);
        assertThat(eventStorage.getUserEvents(newUser1.getId()).get(0).getOperation().toString()).isEqualTo("ADD");

        eventStorage.addEvent(newUser1.getId(), newUser2.getId(), EventType.FRIEND, EventOperation.REMOVE);
        assertThat(eventStorage.getUserEvents(newUser1.getId()).get(1).getOperation().toString()).isEqualTo("REMOVE");
    }

    @Test
    public void insertAddRemoveUpdateReviewInDb() {
        User newUser1 = User.builder()
                .login("Iris")
                .name("Melissa")
                .email("catcat@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userStorage.create(newUser1);

        Film newFilm1 = Film.builder()
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

        newFilm1.getMpa().setId(1);
        filmStorage.create(newFilm1);

        Review newReview = Review.builder()
                .content("Лучший фильм!")
                .isPositive(true)
                .userId(newUser1.getId())
                .filmId(newFilm1.getId())
                .build();
        reviewDbStorage.create(newReview);

        eventStorage.addEvent(newUser1.getId(), newReview.getReviewId(), EventType.REVIEW, EventOperation.ADD);
        assertThat(eventStorage.getUserEvents(newUser1.getId()).get(0).getOperation().toString()).isEqualTo("ADD");

        eventStorage.addEvent(newUser1.getId(), newReview.getReviewId(), EventType.REVIEW, EventOperation.UPDATE);
        assertThat(eventStorage.getUserEvents(newUser1.getId()).get(1).getOperation().toString()).isEqualTo("UPDATE");

        eventStorage.addEvent(newUser1.getId(), newReview.getReviewId(), EventType.REVIEW, EventOperation.REMOVE);
        assertThat(eventStorage.getUserEvents(newUser1.getId()).get(2).getOperation().toString()).isEqualTo("REMOVE");
    }
}
