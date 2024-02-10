drop table IF EXISTS
MPARating,
DIRECTORS,
GENRES,
FILMS,
FILM_GENRE,
FILM_DIRECTOR,
USERS,
LIKES,
FRIENDS,
EVENTTYPES,
EVENTOP,
EVENTLOG,
REVIEW,
REVIEW_LIKE,
REVIEW_DISLIKE;

CREATE TABLE IF NOT EXISTS MPARating
(
  id            INT NOT NULL PRIMARY KEY,
  rating_name   VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS DIRECTORS
(
  id            INT NOT NULL PRIMARY KEY auto_increment,
  director_name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS GENRES
(
  id            INT NOT NULL PRIMARY KEY,
  genre_name    VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS FILMS
(
  id            INT NOT NULL PRIMARY KEY auto_increment,
  name          VARCHAR(255),
  description   VARCHAR(255),
  release_date  DATE,
  duration      INT,
  rating        INT default 0,
  mpa_rating_id INT REFERENCES MPARating(id)
);

CREATE TABLE IF NOT EXISTS FILM_GENRE
(
  film_id       INT REFERENCES FILMS(id) ON DELETE CASCADE,
  genre_id      INT REFERENCES GENRES(id) ON DELETE CASCADE,
  UNIQUE (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS FILM_DIRECTOR
(
  film_id       INT REFERENCES FILMS(id) ON DELETE CASCADE,
  director_id      INT REFERENCES DIRECTORS(id) ON DELETE CASCADE,
  UNIQUE (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS USERS
(
  id            INT NOT NULL PRIMARY KEY auto_increment,
  email         VARCHAR(255) NOT NULL UNIQUE,
  login         VARCHAR(255) NOT NULL UNIQUE,
  name          VARCHAR(255),
  birthday      DATE
);

CREATE TABLE IF NOT EXISTS LIKES
(
  film_id       INT NOT NULL REFERENCES FILMS(id) ON DELETE CASCADE,
  user_id       INT NOT NULL REFERENCES USERS(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS FRIENDS
(
  user_id       INT NOT NULL REFERENCES USERS(id) ON DELETE CASCADE,
  friend_id     INT NOT NULL REFERENCES USERS(id) ON DELETE CASCADE,
  friendship    VARCHAR(255) DEFAULT 'unconfirmed'
);

CREATE TABLE IF NOT EXISTS EVENTTYPES
(
  id            INT NOT NULL PRIMARY KEY auto_increment,
  name          VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS EVENTOP
(
  id            INT NOT NULL PRIMARY KEY auto_increment,
  name          VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS EVENTLOG
(
  id            INT NOT NULL PRIMARY KEY auto_increment,
  event_time    TIMESTAMP NOT NULL,
  user_id       INT NOT NULL REFERENCES USERS(id),
  event_type    INT NOT NULL REFERENCES EVENTTYPES(id),
  operation     INT NOT NULL REFERENCES EVENTOP(id),
  entity_id     INT NOT NULL
);

CREATE TABLE IF NOT EXISTS REVIEW (
    REVIEW_ID INT NOT NULL AUTO_INCREMENT,
    CONTENT VARCHAR(200) NOT NULL,
    IS_POSITIVE BOOLEAN NOT NULL,
    USER_ID INT NOT NULL,
    FILM_ID INT NOT NULL,
    USEFUL INT NOT NULL DEFAULT 0,
    PRIMARY KEY (REVIEW_ID),
    FOREIGN KEY (USER_ID) REFERENCES USERS(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (FILM_ID) REFERENCES FILMS(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS REVIEW_LIKE (
    REVIEW_ID INT NOT NULL,
    USER_ID INT NOT NULL,
    PRIMARY KEY (REVIEW_ID, USER_ID),
    FOREIGN KEY (USER_ID) REFERENCES USERS(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (REVIEW_ID) REFERENCES REVIEW(REVIEW_ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS REVIEW_DISLIKE (
    REVIEW_ID INT NOT NULL,
    USER_ID INT NOT NULL,
    PRIMARY KEY (REVIEW_ID, USER_ID),
    FOREIGN KEY (USER_ID) REFERENCES USERS(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (REVIEW_ID) REFERENCES REVIEW(REVIEW_ID) ON DELETE CASCADE ON UPDATE CASCADE
);
