package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private T[] next;
    private int nextFirst;
    private int nextLast;
    public ArrayDeque() {
        next = (T[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }
    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        if (nextFirst < nextLast - 1 ) {
            System.arraycopy(next, nextFirst + 1, a, 1, size);
            nextFirst = 0;
            nextLast = size + 1;
        } else {
            System.arraycopy(next, 0, a, 0, nextLast);
            System.arraycopy(next, nextFirst + 1, a, capacity + nextFirst + 1 - next.length, next.length - nextFirst - 1);
            nextFirst = capacity + nextFirst - next.length;
        }
        next = a;
    }
    private int minusOne(int index) {
        if (index == 0) {
            return next.length - 1;
        } else {
            return index - 1;
        }
    }
    private int plusOne(int index) {
        if (index == next.length - 1) {
            return  0;
        } else {
            return index + 1;
        }
    }
    @Override
    public void addFirst(T item) {
        if (nextFirst == nextLast - 1 && next[nextFirst] != null) {
            resize(size * 2);
        }
        next[nextFirst] = item;
        nextFirst = minusOne(nextFirst);
        size += 1;
    }
    @Override
    public void addLast(T item) {
        if (nextFirst == nextLast - 1 && next[nextFirst] != null) {
            resize(size * 2);
        }
        next[nextLast] = item;
        nextLast = plusOne(nextLast);
        size += 1;
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        for (int i = nextFirst + 1; i < next.length; i++) {
            System.out.print(next[i] + " ");
        }
        for (int i = 0; i < nextLast; i++) {
            System.out.print(next[i] + " ");
        }
        System.out.println();
    }
    private double usageRatio() {
        return (double) size / (double) next.length;
    }
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        nextFirst = plusOne(nextFirst);
        T result = next[nextFirst];
        next[nextFirst] = null;
        size -= 1;
        if (usageRatio() < 0.25 && next.length >= 16) {
            resize(next.length / 2);
        }
        return result;
    }
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        nextLast = minusOne(nextLast);
        T result = next[nextLast];
        next[nextLast] = null;
        size -= 1;
        if (usageRatio() < 0.25 && next.length >= 16) {
            resize(next.length / 2);
        }
        return result;
    }
    @Override
    public T get(int index) {
        if (index >= next.length) {
            return null;
        }
        if (nextFirst + index + 1 < next.length) {
            return next[nextFirst + index + 1];
        } else {
            return next[nextFirst + index + 1 - next.length];
        }
    }
    private class ArrayDequeIterator implements Iterator<T> {
        private int pos;
        ArrayDequeIterator() {
            pos = 0;
        }
        @Override
        public boolean hasNext() {
            return pos < size;
        }
        @Override
        public T next() {
            T returnItem = get(pos);
            pos += 1;
            return returnItem;
        }
    }
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (this == o) {
            return true;
        } else if (!(o instanceof ArrayDeque)) {
            return false;
        }
        ArrayDeque<T> other = (ArrayDeque<T>) o;
        if (this.size() != other.size()) {
            return false;
        }
        Iterator<T> oIter = other.iterator();
        for (T item : this) {
            if (!item.equals(oIter.next())) {
                return false;
            }
        }
        return true;
    }
}
