package my.interview.practice.collections;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.entry;

public class CollectionsTest {

    @Test
    void arrayListInitTest() {
        // Default Creation
        List<Integer> numbers11 = new ArrayList<>(10);
        System.out.println(numbers11);
        List<Integer> numbers12 = Collections.emptyList();
        System.out.println(numbers12);
        List<Integer> numbers13 = Collections.singletonList(1);
        System.out.println(numbers13);
        // Init Creation with pre-defined values
        List<Integer> numbers2 = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        System.out.println(numbers2);

        // Init creation with pre-defined default values
        List<Integer> numbers3 = new ArrayList<>(Collections.nCopies(10, 0));
        numbers3.add(5);
        // Synchronized Wrapper
        List<Integer> numbers3s = Collections.synchronizedList(numbers3);
        // Unmodifiable Wrapper
        List<Integer> numbers3u = Collections.unmodifiableList(numbers3);
        System.out.println(numbers3);
    }

    @Test
    void collectionsTest() {
        List<Integer> numbers2 = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        System.out.println("Created -> " + numbers2);
        Collections.shuffle(numbers2);
        System.out.println("Shuffled -> " + numbers2);
        Collections.sort(numbers2);
        System.out.println("Sorted -> " + numbers2);

        // Must be in ascending order to Binary Search
        int i = Collections.binarySearch(numbers2, 5);
        System.out.println("Searched 5, Found at Index --> " + i);
        Collections.reverse(numbers2);
        System.out.println("Reversed --> " + numbers2);

//        List<String> list = new ArrayList<>(List.of("A", "B", "C"));
//        for (String s : list) {
//            System.out.println("Trying to remove: " + s);
//            list.remove(s); // ConcurrentModificationException!
//        }

        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(List.of("A","B","C"));
        for (String s : list) {
            System.out.println("Trying to remove: " + s);
            list.remove(s); // No exception — iterates over a snapshot
        }
        for (String s : list) {
            System.out.println("Trying to remove: " + s);
        }
    }

    @Test
    void mapInitTest2() {
        // immutable - Cannot add values later. Put will fail
        Map<Integer, String> immutableMap1 = Map.ofEntries(
                entry(1, "a"),
                entry(2, "b"),
                entry(3, "c"));
        System.out.println(immutableMap1);

        // immutable - Cannot add values later. Put will fail
        Map<Integer, String> immutableMap2 = Map.copyOf(immutableMap1);
        System.out.println(immutableMap2);

        // immutable - Cannot add values later. Put will fail. Works for 5 entries
        Map<Integer, String> immutableMap3 = Map.of(1, "a", 2, "b", 3, "c");
        System.out.println(immutableMap3);

        Map<String, String> map4 = new HashMap<String, String>() {{
            put("1", "1");
            put("2", "b");
        }};
        map4.put("3", "c");
        System.out.println(map4);

        Map<Integer, String> map5 = Stream
                .of(
                        new AbstractMap.SimpleEntry<>(4, "a"),
                        new AbstractMap.SimpleEntry<>(5, "b"),
                        new AbstractMap.SimpleEntry<>(3, "c"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        System.out.println(map5);

        // immutable - Cannot add values later. Put will fail
        TreeMap<Integer, String> immutableMap4 = new TreeMap<>(Map.ofEntries(
                entry(1, "a"),
                entry(2, "b"),
                entry(3, "c")));
        System.out.println(immutableMap4);

    }
}

