package io.github.clojang.gradldromus.demo;

import org.junit.Assume;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

/**
 * Demo tests that showcase skipped/ignored test scenarios.
 */
public class SkippedDemoTests {
    
    @Test
    @Ignore("This test is intentionally ignored")
    public void testIgnoredTest() {
        fail("This should never run");
    }
    
    @Test
    @Ignore("Another ignored test with a longer description explaining why it's disabled")
    public void testAnotherIgnoredTest() {
        assertEquals("This won't execute", 1, 2);
    }
    
    @Test
    public void testAssumptionFailure() {
        // This will cause the test to be skipped
        Assume.assumeTrue("Skipping test due to assumption", false);
        fail("This should not be reached");
    }
    
    @Test
    public void testConditionalSkip() {
        // Skip on certain conditions
        String os = System.getProperty("os.name").toLowerCase();
        Assume.assumeFalse("Skipping on Windows", os.contains("win"));
        
        assertTrue("This test runs on non-Windows systems", true);
    }
    
    @Test
    public void testEnvironmentBasedSkip() {
        // Skip if environment variable is not set
        String ciEnvironment = System.getenv("CI");
        Assume.assumeNotNull("Skipping because CI environment is not set", ciEnvironment);
        
        assertTrue("This only runs in CI", true);
    }
    
    @Test
    public void testJavaVersionSkip() {
        // Skip on older Java versions
        String javaVersion = System.getProperty("java.version");
        Assume.assumeTrue("Requires Java 11+", 
            javaVersion.startsWith("11") || 
            javaVersion.startsWith("1.") == false);
        
        assertTrue("Modern Java feature test", true);
    }
    
    @Test
    public void testPassingTest() {
        // This should pass to show mixed results
        assertEquals("This test should pass", 42, 42);
    }
    
    @Test
    public void testFailingTest() {
        // This should fail to show mixed results
        assertFalse("This test should fail", true);
    }
}