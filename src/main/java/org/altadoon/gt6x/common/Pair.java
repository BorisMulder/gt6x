package org.altadoon.gt6x.common;

public class Pair<K, V> {
    public K key;
    public V value;

    public Pair(K k, V v) {
        this.key = k; this.value = v;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
