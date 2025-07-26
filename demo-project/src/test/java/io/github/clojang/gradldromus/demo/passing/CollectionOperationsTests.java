package io.github.clojang.gradldromus.demo.passing;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

/**
 * Collection operations and data structure tests - all designed to PASS.
 */
public class CollectionOperationsTests {
    
    @Test
    public void testListOperations() {
        List<String> list = new ArrayList<>();
        list.add("first");
        list.add("second");
        list.add("third");
        
        assertEquals("List size should be 3", 3, list.size());
        assertEquals("First element should be 'first'", "first", list.get(0));
        assertTrue("List should contain 'second'", list.contains("second"));
    }
    
    @Test
    public void testSetOperations() {
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        set.add(2); // Duplicate should be ignored
        set.add(3);
        
        assertEquals("Set size should be 3 (duplicates ignored)", 3, set.size());
        assertTrue("Set should contain 1", set.contains(1));
        assertTrue("Set should contain 2", set.contains(2));
        assertTrue("Set should contain 3", set.contains(3));
    }
    
    @Test
    public void testMapOperations() {
        Map<String, Integer> map = new HashMap<>();
        map.put("apple", 5);
        map.put("banana", 3);
        map.put("cherry", 8);
        
        assertEquals("Map size should be 3", 3, map.size());
        assertEquals("Apple count should be 5", Integer.valueOf(5), map.get("apple"));
        assertTrue("Map should contain key 'banana'", map.containsKey("banana"));
        assertTrue("Map should contain value 8", map.containsValue(8));
    }
    
    @Test
    public void testArrayOperations() {
        int[] numbers = {1, 2, 3, 4, 5};
        
        assertEquals("Array length should be 5", 5, numbers.length);
        assertEquals("First element should be 1", 1, numbers[0]);
        assertEquals("Last element should be 5", 5, numbers[4]);
        
        // Calculate sum
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        assertEquals("Sum should be 15", 15, sum);
    }
    
    @Test
    public void testListSorting() {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(3);
        numbers.add(1);
        numbers.add(4);
        numbers.add(2);
        
        Collections.sort(numbers);
        
        assertEquals("First element should be 1", Integer.valueOf(1), numbers.get(0));
        assertEquals("Second element should be 2", Integer.valueOf(2), numbers.get(1));
        assertEquals("Third element should be 3", Integer.valueOf(3), numbers.get(2));
        assertEquals("Fourth element should be 4", Integer.valueOf(4), numbers.get(3));
    }
    
    @Test
    public void testListFiltering() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> evenNumbers = new ArrayList<>();
        
        for (Integer number : numbers) {
            if (number % 2 == 0) {
                evenNumbers.add(number);
            }
        }
        
        assertEquals("Should have 5 even numbers", 5, evenNumbers.size());
        assertTrue("Should contain 2", evenNumbers.contains(2));
        assertTrue("Should contain 4", evenNumbers.contains(4));
        assertFalse("Should not contain 3", evenNumbers.contains(3));
    }
    
    @Test
    public void testCollectionConversion() {
        List<String> list = Arrays.asList("a", "b", "c", "d");
        Set<String> set = new HashSet<>(list);
        
        assertEquals("Set should have same size as list", list.size(), set.size());
        
        List<String> backToList = new ArrayList<>(set);
        assertEquals("Converted back to list should have same size", set.size(), backToList.size());
    }
}
