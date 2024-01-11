CREATE TABLE IF NOT EXISTS MPARating
(
  id            INT PRIMARY KEY,
  rating_name   VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS FILMS
(
  id            INT PRIMARY KEY auto_increment,
  name          VARCHAR(255),
  description   VARCHAR(255),
  release_date  DATE,
  duration      INT,
  rating        INT,
  mpa_rating_id INT REFERENCES MPARating(id)
);

CREATE TABLE IF NOT EXISTS USERS
(
  id            INT PRIMARY KEY auto_increment,
  email         VARCHAR(255) NOT NULL UNIQUE,
  login         VARCHAR(255)NOT NULL UNIQUE,
  name          VARCHAR(255),
  birthday      DATE,
  deleted bool  default false
);

CREATE TABLE IF NOT EXISTS LIKES
(
  film_id       INT NOT NULL REFERENCES FILMS(id),
  user_id       INT NOT NULL REFERENCES USERS(id)
);

CREATE TABLE IF NOT EXISTS FRIENDS
(
  id            INT PRIMARY KEY auto_increment,
  user_id       INT NOT NULL REFERENCES USERS(id),
  friend_id     INT NOT NULL REFERENCES USERS(id),
  status        VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS GENRES
(
  id            INT NOT NULL PRIMARY KEY,
  genre_name    VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS FILM_GENRE
(
  id            INT NOT NULL PRIMARY KEY auto_increment,
  film_id       INT REFERENCES FILMS(id),
  genre_id      INT REFERENCES GENRES(id)
);







