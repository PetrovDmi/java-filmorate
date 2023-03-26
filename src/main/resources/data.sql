DELETE FROM Genre;
DELETE FROM User;
DELETE FROM Film;

ALTER TABLE User ALTER COLUMN userId RESTART WITH 1;
ALTER TABLE Film ALTER COLUMN filmId RESTART WITH 1;
ALTER TABLE Genre ALTER COLUMN genreId RESTART WITH 1;

MERGE INTO Mpa KEY(mpaId )
    VALUES (1, 'G', 'Нет возрастных ограничений'),
           (2, 'PG', 'Рекомендуется присутствие родителей'),
           (3, 'PG-13', 'Детям до 13 лет просмотр не желателен'),
           (4, 'R', 'Лицам до 17 лет обязательно присутствие взрослого'),
           (5, 'NC-17', 'Лицам до 18 лет просмотр запрещен');

MERGE INTO Genre KEY(genreId)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');