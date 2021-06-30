package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private final double maxLoadFactor;
    private Set<K> keys;

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(16);
        maxLoadFactor = 0.75;
        keys = new HashSet<>();
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
        maxLoadFactor = 0.75;
        keys = new HashSet<>();
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        maxLoadFactor = maxLoad;
        keys = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] myBuckets = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            myBuckets[i] = createBucket();
        }
        return myBuckets;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
        buckets = createTable(16);
        keys.clear();
    }

    private int getBucket(K key, int bucketSize) {
        return (key.hashCode() & 0x7fffffff) % bucketSize;
    }

    private int getBucket(K key) {
        return getBucket(key, buckets.length);
    }

    private Node getNode(K key) {
        for (Node n : buckets[getBucket(key)]) {
            if (n.key.equals(key)) {
                return n;
            }
        }
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        if (getNode(key) != null) {
            return true;
        }
        return false;
    }

    @Override
    public V get(K key) {
        if (containsKey(key)) {
            return getNode(key).value;
        }
        return null;
    }

    @Override
    public int size() {
        return keys.size();
    }

    private Collection<Node>[] resize(int newSize) {
        Collection<Node>[] newBuckets;
        newBuckets = createTable(newSize);
        for (K key : keys) {
            newBuckets[getBucket(key, newSize)].add(getNode(key));
        }
        return newBuckets;
    }

    @Override
    public void put(K key, V value) {
        if (containsKey(key)) {
            getNode(key).value = value;
        }
        buckets[getBucket(key)].add(createNode(key, value));
        keys.add(key);
        if ((double) keys.size() / buckets.length >= maxLoadFactor) {
            buckets = resize(buckets.length * 2);
        }
    }

    @Override
    public Set<K> keySet() {
        return keys;
    }

    @Override
    public V remove(K key) {
        if (containsKey(key)) {
            V result = getNode(key).value;
            buckets[getBucket(key)].remove(getNode(key));
            keys.remove(key);
            return result;
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        if (containsKey(key)) {
            V result = getNode(key).value;
            if (result != value) {
                return null;
            }
            buckets[getBucket(key)].remove(getNode(key));
            keys.remove(key);
            return result;
        }
        return null;
    }

    private class MyHashMapIterator implements Iterator<K> {
        Iterator<K> keysIterator;

        MyHashMapIterator() {
            keysIterator = keys.iterator();
        }

        @Override
        public boolean hasNext(){
            return keysIterator.hasNext();
        }

        @Override
        public K next() {
            return keysIterator.next();
        }
    }

    @Override
    public Iterator<K> iterator(){
        return new MyHashMapIterator();
    }

}
