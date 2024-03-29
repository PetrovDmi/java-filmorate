package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;

abstract class Controller<T> {
    public abstract T create(T o);

    public abstract T put(T o);

    public abstract Collection<T> getAll();
}
