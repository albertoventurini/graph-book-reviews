package com.albertoventurini.graphs.bookreviews;

import java.util.Objects;

/**
 * This class represents a pair of values.
 */
public class Pair<T1, T2> {

    public final T1 first;
    public final T2 second;

    private Pair(final T1 first, final T2 second) {
        this.first = first;
        this.second = second;
    }

    public static <U1, U2> Pair<U1, U2> of(final U1 first, final U2 second) {
        return new Pair<>(first, second);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }

    public T1 first() {
        return first;
    }

    public T2 second() {
        return second;
    }
}