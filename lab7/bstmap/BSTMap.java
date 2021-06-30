package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private int size;
    private BSTNode root;

    public BSTMap() {
        size = 0;
    }

    private class BSTNode {
        K key;
        V value;
        boolean color; // black = false; red = true
        BSTNode left;
        BSTNode right;
        BSTNode(K key, V value, boolean color) {
            this.key = key;
            this.value = value;
            this.color = color;
        }
    }

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    private BSTNode get(K key, BSTNode node) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            return node;
        } else if (cmp > 0) {
            return get(key, node.right);
        } else {
            return get(key, node.left);
        }
    }

    @Override
    public boolean containsKey(K key) {
        return get(key, root) != null;
    }

    @Override
    public V get(K key) {
        BSTNode resultNode = get(key, root);
        if (resultNode == null) {
            return null;
        }
        return resultNode.value;
    }

    @Override
    public int size() {
        return size;
    }

    private boolean isRed(BSTNode node) {
        return (node != null && node.color);
    }

    private BSTNode rotateLeft(BSTNode node) {
        BSTNode temp = node.right;
        node.right = node.right.left;
        temp.left = node;
        boolean tempColor = temp.color;
        temp.color = node.color;
        node.color = tempColor;
        return temp;
    }

    private BSTNode rotateRight(BSTNode node) {
        BSTNode temp = node.left;
        node.left = node.left.right;
        temp.right = node;
        boolean tempColor = temp.color;
        temp.color = node.color;
        node.color = tempColor;
        return temp;
    }

    private void colorFlip(BSTNode node) {
        node.color = !node.color;
        node.left.color = !node.left.color;
        node.right.color = !node.right.color;
    }

    private BSTNode put(K key, V value, BSTNode node) {
        if (node == null) {
            return new BSTNode(key, value, true);
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            node.value = value;
        } else if (cmp > 0) {
            node.right = put(key, value, node.right);
        } else {
            node.left = put(key, value, node.left);
        }
        if (isRed(node.right) && !isRed(node.left)) {
            node = rotateLeft(node);
        }
        if (isRed(node.left) && isRed(node.left.left)) {
            node = rotateRight(node);
        }
        if (isRed(node.left) && isRed(node.right)) {
            colorFlip(node);
        }
        return node;
    }

    @Override
    public void put(K key, V value) {
        root = put(key, value, root);
        size += 1;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    private void printInOrder(BSTNode node) {
        if (node == null) {
            return;
        }
        printInOrder(node.left);
        System.out.println(node.key + " " + node.value);
        printInOrder(node.right);
    }

    public void printInOrder() {
        printInOrder(root);
    }

}
