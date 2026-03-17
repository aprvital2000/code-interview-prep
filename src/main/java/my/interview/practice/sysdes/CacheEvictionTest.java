package my.interview.practice.sysdes;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class CacheEvictionTest {

    @Test
    void testLruCache() {
        LruCache cache = new LruCache();
        cache.put("1", "v1");
        cache.put("2", "v2");
        cache.put("3", "v3");
        cache.put("4", "v4");
        // Before -> Order 1, 2, 3, 4
        cache.get("1");
        // After -> Order 2, 3, 4, 1
        cache.put("5", "v5");
        // After -> Order 3, 4, 1, 5

        System.out.println(cache);
    }

    @Test
    void testMruCache() {
        MruCache cache = new MruCache();
        cache.put("1", "v1");
        cache.put("2", "v2");
        cache.put("3", "v3");
        cache.put("4", "v4");
        // Before -> Order 1, 2, 3, 4
        cache.get("1");
        // After -> Order 2, 3, 4, 1
        cache.put("5", "v5");
        // After -> Order 2, 3, 4, 5

        System.out.println(cache);
    }

    @Test
    void testFifoCache() {
        FifoCache cache = new FifoCache();
        cache.put("1", "v1");
        cache.put("2", "v2");
        cache.put("3", "v3");
        cache.put("4", "v4");
        // Before -> Order 1, 2, 3, 4
        cache.get("1");
        // After -> Order 1, 2, 3, 4
        cache.put("5", "v5");
        // After -> Order 2, 3, 4, 5

        System.out.println(cache);
    }

    @Test
    void testTtlCache() throws InterruptedException {
        TtlCache cache = new TtlCache();
        cache.put("1", "v1");
        cache.put("2", "v2");
        cache.put("3", "v3");
        cache.put("4", "v4");
        Thread.sleep(5);
        // Before -> Order 1, 2, 3, 4
        cache.get("1");
        // After -> Order 1, 2, 3, 4
        cache.put("5", "v5");
        // After -> Order 2, 3, 4, 5

        System.out.println(cache);
    }

    @Test
    void testLfuCache() {
        LfuCache cache = new LfuCache();
        cache.put("1", "v1");
        cache.put("2", "v2");
        cache.put("3", "v3");
        cache.put("4", "v4");

        cache.get("1");
        cache.get("1");
        cache.get("1");
        cache.get("2");
        cache.get("2");
        cache.get("2");
        cache.get("3");
        cache.get("3");

        cache.put("5", "v5");
        System.out.println(cache);
    }

    /**
     * Cache Eviction Policy - Least Recently Used (LRU)
     * Removes the item that has not been accessed for the longest period of time.
     * Uses LinkedList to maintain least recently accessed
     * Java LinkedHashMap can be used instead of LruCache
     */
    static class LruCache {

        private final int capacity = 4;
        private final Map<String, String> cache = new HashMap<>(capacity);
        private final LinkedList<String> queue = new LinkedList<>();

        public String get(String key) {
            String value = cache.get(key);
            if (value != null) {
                moveToTop(key);
            }
            return value;
        }

        public void put(String key, String value) {
            if (!cache.containsKey(key)) {
                if (cache.size() >= capacity) {
                    cache.remove(queue.getFirst());
                    queue.removeFirst();
                }
                cache.put(key, value);
                queue.addLast(key);
            } else {
                cache.put(key, value);
                moveToTop(key);
            }
        }

        private void moveToTop(String key) {
            queue.remove(key);
            queue.addLast(key);
        }

        @Override
        public String toString() {
            return "cache == " + cache + ", queue = " + queue;
        }
    }

    /**
     * Cache Eviction Policy - Most Recently Used (MRU)
     * Ideal for scenarios where older data is more likely to be accessed again.
     * Uses LinkedList to maintain most recently accessed
     * Java LinkedHashMap can be used instead of MruCache
     */
    static class MruCache {

        private static final int capacity = 4;
        private static final Map<String, String> cache = new HashMap<>(capacity);
        private static final LinkedList<String> queue = new LinkedList<>();

        public String get(String key) {
            String value = cache.get(key);
            if (value != null) {
                moveToTop(key);
            }
            return value;
        }

        public void put(String key, String value) {
            if (!cache.containsKey(key)) {
                if (cache.size() >= capacity) {
                    cache.remove(queue.getLast());
                    queue.removeLast();
                }
                cache.put(key, value);
                queue.addLast(key);
            } else {
                cache.put(key, value);
                moveToTop(key);
            }
        }

        private void moveToTop(String key) {
            queue.remove(key);
            queue.addLast(key);
        }

        @Override
        public String toString() {
            return "cache == " + cache + ", queue = " + queue;
        }
    }

    /**
     * Cache Eviction Policy - First In First Out (FIFO)
     * Removes the oldest added item, regardless of access frequency.
     * Uses LinkedList to order of insertion
     */
    static class FifoCache {

        private static final int capacity = 4;
        private static final Map<String, String> cache = new HashMap<>(capacity);
        private static final LinkedList<String> queue = new LinkedList<>();

        public String get(String key) {
            return cache.get(key);
        }

        public void put(String key, String value) {
            if (!cache.containsKey(key)) {
                if (cache.size() >= capacity) {
                    cache.remove(queue.getFirst());
                    queue.removeFirst();
                }
                cache.put(key, value);
                queue.addLast(key);
            } else {
                cache.put(key, value);
            }
        }

        @Override
        public String toString() {
            return "cache == " + cache + ", queue = " + queue;
        }
    }

    /**
     * Cache Eviction Policy - Time To Live (TTL)
     * Evicts items once their predefined lifespan (time) expires, often used in databases like Redis
     * Uses LinkedList to order of insertion
     */
    static class TtlCache {

        private static final long ttl_nanos = 5L * 1000 * 1000 * 1000;
        private static final Map<String, String> cache = new HashMap<>();
        private static final Map<String, Long> queue = new HashMap<>();

        public String get(String key) {
            String value = cache.get(key);
            long time = queue.get(key);
            if (time >= System.nanoTime()) {
                queue.remove(key);
                cache.remove(key);
                value = null;
            }
            return value;
        }

        public void put(String key, String value) {
            cache.put(key, value);
            queue.put(key, System.nanoTime() + ttl_nanos);
        }

        @Override
        public String toString() {
            return "cache == " + cache + ", queue = " + queue;
        }
    }

    /**
     * Cache Eviction Policy - Least Frequently Used (LFU)
     * Removes the item that has not been accessed the least number of times
     * Uses LinkedList to order of insertion
     */
    static class LfuCache {

        private static final int capacity = 4;
        private static final Map<String, String> cache = new HashMap<>();
        private static final Map<String, Integer> countMap = new HashMap<>();
        private static final TreeMap<Integer, LinkedList<String>> frequencyMap = new TreeMap<>();

        public String get(String key) {
            String value = cache.get(key);
            if (value == null) {
                return null;
            }

            int frequency = countMap.get(key);
            countMap.put(key, frequency + 1);

            frequencyMap
                    .get(frequency)
                    .remove(key);
            //remove frequency from map if list is empty
            if (frequencyMap
                    .get(frequency)
                    .isEmpty())
                frequencyMap.remove(frequency);

            frequencyMap
                    .computeIfAbsent(frequency + 1, k -> new LinkedList<>())
                    .add(key);
            return value;
        }

        public void put(String key, String value) {
            if (!cache.containsKey(key)) {
                if (cache.size() >= capacity) {
                    // remove first element from the list element with minimum frequency
                    int lowestCount = frequencyMap.firstKey();
                    String keyToDelete = frequencyMap
                            .get(lowestCount)
                            .removeFirst();

                    //remove frequency from map if list is empty
                    if (frequencyMap
                            .get(lowestCount)
                            .isEmpty())
                        frequencyMap.remove(lowestCount);

                    cache.remove(keyToDelete);
                    countMap.remove(keyToDelete);
                }
                cache.put(key, value);
                countMap.put(key, 1);
                frequencyMap
                        .computeIfAbsent(1, k -> new LinkedList<>())
                        .add(key);
            } else {
                cache.put(key, value);
                int frequency = countMap.get(key);
                countMap.put(key, frequency + 1);
                frequencyMap
                        .computeIfAbsent(frequency + 1, k -> new LinkedList<>())
                        .add(key);
            }
        }

        @Override
        public String toString() {
            return "cache == " + cache + ", countMap = " + countMap + ", frequency" + frequencyMap;
        }
    }
}
