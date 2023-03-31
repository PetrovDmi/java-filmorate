package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    @PositiveOrZero
    private int id;
    @NotBlank(message = "Имя должно содержать буквенные символы. ")
    private String name;
    @NotBlank
    @Size(max = 200, message = "Описание фильма не должно превышать 200 символов. ")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма не может быть отрицательной. ")
    private int duration;
    @NotNull
    private Mpa mpa;
    private HashSet<Integer> likes = new HashSet<>();
    private List<FilmGenre> genres = new ArrayList<>();

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, @NotNull Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(int id, Film film) {
        this.id = id;
        this.name = film.name;
        this.description = film.description;
        this.releaseDate = film.releaseDate;
        this.duration = film.duration;
        this.mpa = film.getMpa();
        this.genres = new ArrayList<>(film.getGenres());
        this.likes = new HashSet<>(film.getLikes());
    }

    public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public void addLike(Integer userId) {
        likes.add(userId);
    }

    public boolean deleteLike(Integer userId) {
        return likes.remove(userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Film)) return false;
        Film film = (Film) o;
        return getId() == film.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
