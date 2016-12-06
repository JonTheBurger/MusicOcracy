package com.musicocracy.fpgk.domain.util;

public class CircularQueue<T> implements SimpleQueue<T>{
    private final int maxSize;
    private T[] queue;
    private int head = 0;
    private int tail = 0;
    private boolean isEmpty = true;
    private int size = 0;

    public CircularQueue(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Queue size must be greater than 0.");
        } else {
            this.maxSize = maxSize;
            queue = (T[])new Object[maxSize];
        }
    }

    @Override
    public void enqueue(T item) {
        queue[tail] = item;
        if (size != maxSize) {
            size++;
        }
        // The place to add is the oldest item in the queue
        if (size != 0 && head == tail) {
            head = (head + 1) % size();
        }
        tail = ((tail + 1) % maxSize);
    }

    @Override
    public T dequeue() {
        if (size == 0) {
            throw new IllegalStateException("Dequeue performed on empty circular queue.");
        } else {
            T item = queue[head];
            head = (head + 1) % size();
            return item;
        }
    }

    @Override
    public boolean contains(T itemToCheck) {
        if (size() != 0) {
            for (T item : queue) {
                if (item.equals(itemToCheck)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }
}
