package io.github.clojang.gradldromus.demo.passing;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * String manipulation and validation tests - all designed to PASS.
 */
public class StringOperationsTests {
    
    @Test
    public void testStringConcatenation() {
        String first = "Hello";
        String second = "World";
        String result = first + " " + second;
        
        assertEquals("String concatenation should work", "Hello World", result);
    }
    
    @Test
    public void testStringLength() {
        String text = "Testing";
        assertEquals("String length should be correct", 7, text.length());
    }
    
    @Test
    public void testStringUpperCase() {
        String text = "lowercase";
        assertEquals("String should convert to uppercase", "LOWERCASE", text.toUpperCase());
    }
    
    @Test
    public void testStringLowerCase() {
        String text = "UPPERCASE";
        assertEquals("String should convert to lowercase", "uppercase", text.toLowerCase());
    }
    
    @Test
    public void testStringContains() {
        String text = "The quick brown fox";
        assertTrue("String should contain 'quick'", text.contains("quick"));
        assertTrue("String should contain 'fox'", text.contains("fox"));
    }
    
    @Test
    public void testStringStartsAndEnds() {
        String text = "Hello World";
        assertTrue("String should start with 'Hello'", text.startsWith("Hello"));
        assertTrue("String should end with 'World'", text.endsWith("World"));
    }
    
    @Test
    public void testStringTrim() {
        String text = "  trimmed  ";
        assertEquals("String should be trimmed", "trimmed", text.trim());
    }
    
    @Test
    public void testStringSplit() {
        String text = "apple,banana,cherry";
        String[] parts = text.split(",");
        
        assertEquals("Should have 3 parts", 3, parts.length);
        assertEquals("First part should be apple", "apple", parts[0]);
        assertEquals("Second part should be banana", "banana", parts[1]);
        assertEquals("Third part should be cherry", "cherry", parts[2]);
    }
}
