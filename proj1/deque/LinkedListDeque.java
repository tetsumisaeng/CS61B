package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private Node sentinel;
    private class Node {
        private T item;
        private Node prev, next;
        Node(T i, Node p, Node n) {
            item = i;
            prev = p;
            next = n;
        }
    }
    public LinkedListDeque() {
        Node helper = new Node(null, null, null);
        sentinel = new Node(null, helper, helper);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }
    @Override
    public void addFirst(T item) {
        sentinel.next = new Node(item, sentinel, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size += 1;
    }
    @Override
    public void addLast(T item) {
        sentinel.prev = new Node(item, sentinel.prev, sentinel);
        sentinel.prev.prev.next = sentinel.prev;
        size += 1;
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        for (Node curr = sentinel.next; curr != sentinel; curr = curr.next) {
            System.out.print(curr.item + " ");
        }
        System.out.println();
    }
    @Override
    public T removeFirst() {
        if (sentinel.next == sentinel)  {
            return null;
        }
        T result = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size -= 1;
        return result;
    }
    @Override
    public T removeLast() {
        if (sentinel.prev == sentinel)  {
            return null;
        }
        T result = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size -= 1;
        return result;
    }
    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        int pos = 0;
        Node curr = sentinel.next;
        while (pos != index) {
            pos += 1;
            curr = curr.next;
        }
        return curr.item;
    }
    private T getRecursiveHelper(Node n, int index) {
        if (index == 0) {
            return n.item;
        } else {
            return getRecursiveHelper(n.next, index - 1);
        }
    }
    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        } else {
            return getRecursiveHelper(sentinel.next, index);
        }
    }
    private class LinkedListDequeIterator implements Iterator<T> {
        private Node pos;
        LinkedListDequeIterator() {
            pos = sentinel.next;
        }
        @Override
        public boolean hasNext() {
            return pos != sentinel;
        }
        @Override
        public T next() {
            T returnItem = pos.item;
            pos = pos.next;
            return returnItem;
        }
    }
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (this == o) {
            return true;
        } else if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        LinkedListDeque<T> other = (LinkedListDeque<T>) o;
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
