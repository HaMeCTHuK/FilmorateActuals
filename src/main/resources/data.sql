merge INTO MPARating (id,rating_name)
            values (1, 'G'),
                   (2, 'PG'),
                   (3, 'PG-13'),
                   (4, 'R'),
                   (5, 'NC-13');

merge INTO GENRES (id,genre_name)
            values  (1, 'Comedy'),
                    (2, 'Drama'),
                    (3, 'Cartoon'),
                    (4, 'Thriller'),
                    (5, 'Documental'),
                    (6, 'Action movie');
