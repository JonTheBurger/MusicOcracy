package com.musicocracy.fpgk.domain.util;

// A class to workaround Java 7's lack of proper closures.
public class Box<T> {
    public T value;

    public Box(T contents) {
        this.value = contents;
    }
}
