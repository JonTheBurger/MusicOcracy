package com.musicocracy.fpgk.domain.util;

public class CircularQueue<T> implements SimpleQueue<T>{
    private final int maxSize;
    private T[] queue;
    private int head = 0;
    private int tail = 0;
    private boolean isEmpty = true;

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
        if (tail != maxSize) {
            if (head == tail && !isEmpty) {
                head++;
            }
            queue[tail++] = item;
            isEmpty = false;
        } else {
            if (head == 0 && maxSize != 1 && !isEmpty) {
                head++;
            }
            queue[0] = item;
            tail = 1;
        }
    }

    @Override
    public T dequeue() {
        if (isEmpty) {
            throw new IllegalStateException("Dequeue performed on empty circular queue.");
        } else if(head == maxSize - 1) {
            head = 0;
            if (head == tail) {
                isEmpty = true;
            }
            return queue[maxSize - 1];
        } else {
            head++;
            if (head == tail) {
                isEmpty = true;
            }
            return queue[head - 1];
        }
    }

    @Override
    public int size() {
        if (isEmpty) {
            return 0;
        }
        else if (tail > head) {
            return tail - head;
        }
        else if (tail == head) {
            return maxSize;
        } else {
            return (maxSize - (head - tail)) - 1;
        }
    }
}
