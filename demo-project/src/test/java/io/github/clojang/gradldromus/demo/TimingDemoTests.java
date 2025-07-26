package io.github.clojang.gradldromus.demo;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Demo tests that showcase timing display and various test durations.
 * Mix of passing and failing tests with different execution times.
 */
public class TimingDemoTests {
    
    @Test
    public void testVeryFastPassing() {
        // Near-instantaneous test
        assertEquals(1, 1);
    }
    
    @Test
    public void testFastPassing() throws InterruptedException {
        Thread.sleep(10);
        assertTrue("This should pass quickly", true);
    }
    
    @Test
    public void testMediumPassing() throws InterruptedException {
        Thread.sleep(100);
        assertEquals("Values should match", "expected", "expected");
    }
    
    @Test
    public void testSlowPassing() throws InterruptedException {
        Thread.sleep(500);
        assertNotNull("Object should exist", new Object());
    }
    
    @Test
    public void testVerySlowPassing() throws InterruptedException {
        Thread.sleep(1000);
        assertTrue("Long running test should pass", true);
    }
    
    @Test
    public void testFastFailing() throws InterruptedException {
        Thread.sleep(25);
        fail("This test fails quickly");
    }
    
    @Test
    public void testMediumFailing() throws InterruptedException {
        Thread.sleep(200);
        assertEquals("This will fail after some time", "expected", "actual");
    }
    
    @Test
    public void testSlowFailing() throws InterruptedException {
        Thread.sleep(750);
        throw new RuntimeException("This test fails after a long delay");
    }
    
    @Test
    public void testComputationalWork() {
        // Simulate some computational work
        long result = 0;
        for (int i = 0; i < 1000000; i++) {
            result += i * i;
        }
        assertTrue("Result should be positive", result > 0);
    }
    
    @Test
    public void testComputationalWorkThatFails() {
        // Simulate computational work that leads to failure
        long result = 0;
        for (int i = 0; i < 500000; i++) {
            result += i;
        }
        assertEquals("This computation result is wrong", 0, result);
    }
    
    @Test
    public void testVariableDelay() throws InterruptedException {
        // Variable delay based on current time
        long delay = System.currentTimeMillis() % 300 + 50; // 50-350ms
        Thread.sleep(delay);
        assertTrue("Variable delay test", true);
    }
    
    @Test
    public void testMethodWithLongName() throws InterruptedException {
        Thread.sleep(150);
        assertTrue("Long method name test passes", true);
    }
    
    @Test
    public void testAnotherMethodWithAVeryLongNameThatShouldTestLineWrappingBehavior() throws InterruptedException {
        Thread.sleep(300);
        fail("This test with a very long name should fail");
    }
}