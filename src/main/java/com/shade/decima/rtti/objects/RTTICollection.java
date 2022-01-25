package com.shade.decima.rtti.objects;

import com.shade.decima.rtti.RTTIType;
import com.shade.decima.util.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RTTICollection<T> implements Iterable<T> {
    private final RTTIType<T> type;
    private final T[] data;

    public RTTICollection(@NotNull RTTIType<T> type, @NotNull T[] data) {
        this.type = type;
        this.data = data;
    }

    @NotNull
    public T get(int index) {
        return data[index];
    }

    public int size() {
        return data.length;
    }

    @NotNull
    public RTTIType<T> getType() {
        return type;
    }

    @NotNull
    public T[] toArray() {
        return data;
    }

    @NotNull
    public Iterator<T> iterator() {
        return new ArrayIterator<>(data);
    }

    @Override
    public String toString() {
        return "RTTICollection{type=" + type + ", data=" + Arrays.toString(data) + '}';
    }

    private static class ArrayIterator<T> implements Iterator<T> {
        private final T[] array;
        private int cursor;

        public ArrayIterator(@NotNull T[] array) {
            this.array = array;
            this.cursor = 0;
        }

        @Override
        public boolean hasNext() {
            return cursor < array.length;
        }

        @Override
        public T next() {
            if (cursor >= array.length) {
                throw new NoSuchElementException();
            }
            return array[cursor++];
        }
    }
}
