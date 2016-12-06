package com.musicocracy.fpgk.domain.util;

public interface SimpleQueue<T> {
    void enqueue(T item);
    T dequeue();
    boolean contains(T itemToCheck);
    int size();
}
