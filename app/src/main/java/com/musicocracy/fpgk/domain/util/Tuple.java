package com.musicocracy.fpgk.domain.util;

public class Tuple<T1, T2> {
    public final T1 one;
    public final T2 two;

    public Tuple(T1 one, T2 two) {
        this.one = one;
        this.two = two;
    }

    @Override
    public String toString() {
        return one.toString() + ", " + two.toString();
    }

    public static<T extends Comparable> Integer compareOne(Tuple<T, ?> lhs, Tuple<T, ?> rhs) {
        return lhs.one.compareTo(rhs.one);
    }

    public static<T extends Comparable> Integer compareTwo(Tuple<?, T> lhs, Tuple<?, T> rhs) {
        return lhs.two.compareTo(rhs.two);
    }
}
