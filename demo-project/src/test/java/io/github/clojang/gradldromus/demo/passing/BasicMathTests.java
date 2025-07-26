package io.github.clojang.gradldromus.demo.passing;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Basic mathematical operations tests - all designed to PASS.
 */
public class BasicMathTests {
    
    @Test
    public void testAddition() {
        assertEquals("2 + 2 should equal 4", 4, 2 + 2);
    }
    
    @Test
    public void testSubtraction() {
        assertEquals("10 - 3 should equal 7", 7, 10 - 3);
    }
    
    @Test
    public void testMultiplication() {
        assertEquals("5 * 6 should equal 30", 30, 5 * 6);
    }
    
    @Test
    public void testDivision() {
        assertEquals("15 / 3 should equal 5", 5, 15 / 3);
    }
    
    @Test
    public void testModulo() {
        assertEquals("17 % 5 should equal 2", 2, 17 % 5);
    }
    
    @Test
    public void testPowerCalculation() {
        int result = 1;
        int base = 2;
        int exponent = 3;
        
        for (int i = 0; i < exponent; i++) {
            result *= base;
        }
        
        assertEquals("2^3 should equal 8", 8, result);
    }
}
