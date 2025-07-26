package io.github.clojang.gradldromus.demo;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Demo tests that showcase various failure modes and exception types.
 * These tests are designed to FAIL to demonstrate the plugin's exception handling capabilities.
 * 
 * Run with: ./gradlew demoTest
 */
public class ExceptionDemoTests {
    
    @Test
    public void testSimpleAssertionFailure() {
        // Simple assertion failure with clear message
        assertEquals("Expected values should match", 42, 24);
    }
    
    @Test
    public void testNullPointerException() {
        // Classic NPE
        String str = null;
        int length = str.length(); // This will throw NPE
    }
    
    @Test
    public void testArrayIndexOutOfBounds() {
        // Array access violation
        int[] array = {1, 2, 3};
        int value = array[10]; // Index out of bounds
    }
    
    @Test
    public void testArithmeticException() {
        // Division by zero
        int result = 42 / 0;
    }
    
    @Test
    public void testCustomExceptionWithMessage() {
        throw new IllegalArgumentException("This is a custom exception with a detailed message explaining what went wrong");
    }
    
    @Test
    public void testCustomExceptionWithoutMessage() {
        throw new IllegalStateException(); // No message
    }
    
    @Test
    public void testNestedExceptionChain() {
        try {
            try {
                throw new IllegalArgumentException("Root cause exception");
            } catch (Exception e) {
                throw new RuntimeException("Middle exception", e);
            }
        } catch (Exception e) {
            throw new AssertionError("Top level exception", e);
        }
    }
    
    @Test
    public void testDeepStackTrace() {
        methodA();
    }
    
    private void methodA() {
        methodB();
    }
    
    private void methodB() {
        methodC();
    }
    
    private void methodC() {
        methodD();
    }
    
    private void methodD() {
        methodE();
    }
    
    private void methodE() {
        methodF();
    }
    
    private void methodF() {
        methodG();
    }
    
    private void methodG() {
        throw new RuntimeException("Exception thrown from deep in the call stack");
    }
    
    @Test
    public void testMultiLineExceptionMessage() {
        String multiLineMessage = "This is a multi-line exception message.\n" +
                                "Line 2: Additional context information.\n" +
                                "Line 3: Even more details about the failure.\n" +
                                "Line 4: Final line of the error description.";
        throw new IllegalStateException(multiLineMessage);
    }
    
    @Test
    public void testExceptionWithSpecialCharacters() {
        throw new RuntimeException("Exception with special chars: !@#$%^&*()_+-={}|[]\\:\";'<>?,./`~");
    }
    
    @Test
    public void testExceptionWithUnicodeCharacters() {
        throw new RuntimeException("Exception with Unicode: 🚨 Error occurred in test 🧪 with émojis and spëcial characters ñ");
    }
    
    @Test
    public void testAssertTrueFailure() {
        assertTrue("This boolean expression should be true but it's false", false);
    }
    
    @Test
    public void testAssertFalseFailure() {
        assertFalse("This boolean expression should be false but it's true", true);
    }
    
    @Test
    public void testAssertNotNullFailure() {
        assertNotNull("Object should not be null", null);
    }
    
    @Test
    public void testAssertEqualsFailure() {
        assertEquals("String values should be equal", "expected", "actual");
    }
    
    @Test
    public void testTimeoutScenario() throws InterruptedException {
        // Simulate a test that might timeout (though JUnit doesn't enforce this by default)
        Thread.sleep(100); // Small delay
        throw new RuntimeException("Test failed after delay");
    }
    
    @Test
    public void testLongClassName() {
        throw new VeryLongClassNameExceptionForTestingPurposesOnly(
            "Exception from a class with a very long name"
        );
    }
    
    // Custom exception class for testing
    public static class VeryLongClassNameExceptionForTestingPurposesOnly extends RuntimeException {
        public VeryLongClassNameExceptionForTestingPurposesOnly(String message) {
            super(message);
        }
    }
}