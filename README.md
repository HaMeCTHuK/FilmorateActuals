# java-filmorate
Template repository for Filmorate project.
![click](DBScheme.png)<br />
Examples:

1) **SELECT * FROM Film;** <br />

Receive all from table Film.

2) **SELECT Film.name, <br />
          Film.description,<br />
          Genre.name AS genre<br />
   FROM Film<br />
   JOIN Film_Genre ON Film.id = Film_Genre.film_id<br />
   JOIN Genre ON Film_Genre.genre_id = Genre.id**;

   
Receive all films with their genres

3) **SELECT Genre.name, <br />
   COUNT(Film_Genre.film_id) AS film_count<br />
   FROM Genre <br />
   LEFT JOIN Film_Genre ON Genre.id = Film_Genre.genre_id <br />
   GROUP BY Genre.id; <br />**

Getting all genres and the number of films in each genre


