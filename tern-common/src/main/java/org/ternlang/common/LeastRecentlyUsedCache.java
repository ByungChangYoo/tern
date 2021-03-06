package org.ternlang.common;

import java.util.Map;
import java.util.Set;

import org.ternlang.common.LeastRecentlyUsedMap.RemovalListener;

public class LeastRecentlyUsedCache<K, V> implements Cache<K, V> {

   private final Map<K, V> cache;

   public LeastRecentlyUsedCache() {
      this(null);
   }

   public LeastRecentlyUsedCache(int capacity) {
      this(null, capacity);
   }

   public LeastRecentlyUsedCache(RemovalListener<K, V> removalListener) {
      this(removalListener, 100);
   }

   public LeastRecentlyUsedCache(RemovalListener<K, V> removalListener, int capacity) {
      this.cache = new LeastRecentlyUsedMap<K, V>(removalListener, capacity);
   }

   @Override
   public synchronized void clear() {
      cache.clear();
   }

   @Override
   public synchronized int size() {
      return cache.size();
   }

   @Override
   public synchronized Set<K> keySet() {
      return cache.keySet();
   }

   @Override
   public synchronized V fetch(K key) {
      return cache.get(key);
   }

   @Override
   public synchronized V cache(K key, V value) {
      return cache.put(key, value);
   }

   @Override
   public synchronized V take(K key) {
      return cache.remove(key);
   }

   @Override
   public synchronized boolean contains(K key) {
      return cache.containsKey(key);
   }

   @Override
   public synchronized boolean isEmpty() {
      return cache.isEmpty();
   }
   
   @Override
   public synchronized String toString() {
      return String.valueOf(cache);
   }
}