package io.github.clojang.gradldromus;

import org.gradle.api.Project;
import org.gradle.api.tasks.testing.TestDescriptor;
import org.gradle.api.tasks.testing.TestResult;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;

public class CustomTestListenerTest {
    
    private Project project;
    private GradlDromusExtension extension;
    private CustomTestListener listener;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream testOut;
    
    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build();
        extension = new GradlDromusExtension();
        listener = new CustomTestListener(project, extension);
        
        // Capture output
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }
    
    @After
    public void tearDown() {
        System.setOut(originalOut);
    }
    
    @Test
    public void testListenerInitialState() {
        assertFalse("Should have no tests initially", listener.hasTests());
    }
    
    @Test
    public void testSuccessfulTest() {
        // Create mock test descriptor and result
        TestDescriptor testDescriptor = new MockTestDescriptor("TestClass", "testMethod");
        TestResult result = new MockTestResult(TestResult.ResultType.SUCCESS, 100, 150);
        
        listener.setCurrentTaskPath(":test");
        listener.beforeTest(testDescriptor);
        listener.afterTest(testDescriptor, result);
        
        assertTrue("Should have tests after processing", listener.hasTests());
        
        String output = testOut.toString();
        assertTrue("Output should contain class name", output.contains("TestClass"));
        assertTrue("Output should contain method name", output.contains("testMethod"));
        assertTrue("Output should contain success symbol", output.contains("💚"));
    }
    
    @Test
    public void testFailedTest() {
        TestDescriptor testDescriptor = new MockTestDescriptor("FailingTest", "testFailure");
        MockTestResult result = new MockTestResult(TestResult.ResultType.FAILURE, 100, 200);
        result.addException(new RuntimeException("Test failed"));
        
        listener.setCurrentTaskPath(":test");
        listener.beforeTest(testDescriptor);
        listener.afterTest(testDescriptor, result);
        
        String output = testOut.toString();
        assertTrue("Output should contain fail symbol", output.contains("💔"));
        assertTrue("Output should contain exception message", output.contains("Test failed"));
    }
    
    @Test
    public void testSkippedTest() {
        TestDescriptor testDescriptor = new MockTestDescriptor("SkippedTest", "testSkipped");
        TestResult result = new MockTestResult(TestResult.ResultType.SKIPPED, 100, 105);
        
        listener.setCurrentTaskPath(":test");
        listener.beforeTest(testDescriptor);
        listener.afterTest(testDescriptor, result);
        
        String output = testOut.toString();
        assertTrue("Output should contain skip symbol", output.contains("💤"));
    }
    
    @Test
    public void testExceptionDisplayConfiguration() {
        // Test with exceptions disabled
        extension.setShowExceptions(false);
        
        TestDescriptor testDescriptor = new MockTestDescriptor("TestClass", "testMethod");
        MockTestResult result = new MockTestResult(TestResult.ResultType.FAILURE, 100, 200);
        result.addException(new RuntimeException("This should not be shown"));
        
        listener.setCurrentTaskPath(":test");
        listener.beforeTest(testDescriptor);
        listener.afterTest(testDescriptor, result);
        
        String output = testOut.toString();
        assertFalse("Output should not contain exception message when disabled", 
                   output.contains("This should not be shown"));
    }
    
    @Test
    public void testTimingDisplay() {
        // Test with timings enabled
        extension.setShowTimings(true);
        
        TestDescriptor testDescriptor = new MockTestDescriptor("TestClass", "testMethod");
        TestResult result = new MockTestResult(TestResult.ResultType.SUCCESS, 100, 250);
        
        listener.setCurrentTaskPath(":test");
        listener.beforeTest(testDescriptor);
        listener.afterTest(testDescriptor, result);
        
        String output = testOut.toString();
        assertTrue("Output should contain timing when enabled", output.contains("ms)"));
    }
    
    @Test
    public void testCustomSymbols() {
        extension.setPassSymbol("✅");
        extension.setFailSymbol("❌");
        extension.setSkipSymbol("⏭");
        
        // Test pass symbol
        TestDescriptor passDescriptor = new MockTestDescriptor("PassTest", "testPass");
        TestResult passResult = new MockTestResult(TestResult.ResultType.SUCCESS, 100, 150);
        
        listener.setCurrentTaskPath(":test");
        listener.beforeTest(passDescriptor);
        listener.afterTest(passDescriptor, passResult);
        
        String output = testOut.toString();
        assertTrue("Output should contain custom pass symbol", output.contains("✅"));
    }
    
    // Mock classes for testing
    private static class MockTestDescriptor implements TestDescriptor {
        private final String className;
        private final String name;
        
        public MockTestDescriptor(String className, String name) {
            this.className = className;
            this.name = name;
        }
        
        @Override
        public String getName() { return name; }
        
        @Override
        public String getDisplayName() { return name; }
        
        @Override
        public String getClassName() { return className; }
        
        @Override
        public boolean isComposite() { return false; }
        
        @Override
        public TestDescriptor getParent() { return null; }
    }
    
    private static class MockTestResult implements TestResult {
        private final ResultType resultType;
        private final long startTime;
        private final long endTime;
        private final java.util.List<Throwable> exceptions = new java.util.ArrayList<>();
        
        public MockTestResult(ResultType resultType, long startTime, long endTime) {
            this.resultType = resultType;
            this.startTime = startTime;
            this.endTime = endTime;
        }
        
        public void addException(Throwable exception) {
            exceptions.add(exception);
        }
        
        @Override
        public ResultType getResultType() { return resultType; }
        
        @Override
        public Throwable getException() { 
            return exceptions.isEmpty() ? null : exceptions.get(0); 
        }
        
        @Override
        public java.util.List<Throwable> getExceptions() { return exceptions; }
        
        @Override
        public long getStartTime() { return startTime; }
        
        @Override
        public long getEndTime() { return endTime; }
        
        @Override
        public long getTestCount() { return 1; }
        
        @Override
        public long getSuccessfulTestCount() { 
            return resultType == ResultType.SUCCESS ? 1 : 0; 
        }
        
        @Override
        public long getFailedTestCount() { 
            return resultType == ResultType.FAILURE ? 1 : 0; 
        }
        
        @Override
        public long getSkippedTestCount() { 
            return resultType == ResultType.SKIPPED ? 1 : 0; 
        }
    }
}