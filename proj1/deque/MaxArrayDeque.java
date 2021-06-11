package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> cp;
    public MaxArrayDeque(Comparator<T> c) {
        super();
        cp = c;
    }
    public T max() {
        return max(cp);
    }
    public T max(Comparator<T> c) {
        if (size() == 0) {
            return null;
        }
        T max = get(0);
        for (T item : this) {
            if (c.compare(item, max) > 0) {
                max = item;
            }
        }
        return max;
    }
}
