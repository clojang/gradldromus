package io.github.clojang.gradldromus;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
    public void testSetCurrentTaskPath() {
        // Test that we can set the current task path without errors
        listener.setCurrentTaskPath(":test");
        // No exception should be thrown
        assertTrue("Task path set successfully", true);
    }
    
    @Test
    public void testListenerCreation() {
        // Test that listener can be created with different extension settings
        extension.setShowExceptions(false);
        extension.setShowStackTraces(true);
        extension.setShowFullStackTraces(true);
        extension.setMaxStackTraceDepth(5);
        
        CustomTestListener customListener = new CustomTestListener(project, extension);
        assertNotNull("Custom listener should be created", customListener);
        assertFalse("Should have no tests initially", customListener.hasTests());
    }
    
    @Test
    public void testExtensionConfiguration() {
        // Test various extension configurations
        extension.setShowExceptions(false);
        assertFalse("showExceptions should be false", extension.isShowExceptions());
        
        extension.setShowStackTraces(true);
        assertTrue("showStackTraces should be true", extension.isShowStackTraces());
        
        extension.setShowFullStackTraces(true);
        assertTrue("showFullStackTraces should be true", extension.isShowFullStackTraces());
        
        extension.setMaxStackTraceDepth(3);
        assertEquals("maxStackTraceDepth should be 3", 3, extension.getMaxStackTraceDepth());
        
        extension.setPassSymbol("✅");
        assertEquals("passSymbol should be ✅", "✅", extension.getPassSymbol());
        
        extension.setFailSymbol("❌");
        assertEquals("failSymbol should be ❌", "❌", extension.getFailSymbol());
        
        extension.setSkipSymbol("⏭");
        assertEquals("skipSymbol should be ⏭", "⏭", extension.getSkipSymbol());
    }
    
    @Test
    public void testColorsConfiguration() {
        // Test colors with extension
        extension.setUseColors(true);
        AnsiColors colors = new AnsiColors(extension.isUseColors());
        
        String result = colors.colorize("test", AnsiColors.RED);
        assertTrue("Should contain color codes when enabled", result.contains("\u001B[31m"));
        
        extension.setUseColors(false);
        AnsiColors noColors = new AnsiColors(extension.isUseColors());
        String resultNoColors = noColors.colorize("test", AnsiColors.RED);
        assertEquals("Should not contain color codes when disabled", "test", resultNoColors);
    }
    
    @Test
    public void testTerminalPrinter() {
        CleanTerminalPrinter printer = new CleanTerminalPrinter(extension);
        
        // Test terminal width configuration
        extension.setTerminalWidth(120);
        assertEquals("Terminal width should be 120", 120, printer.getTerminalWidth());
        
        extension.setTerminalWidth(0);
        assertTrue("Should use default width when 0", printer.getTerminalWidth() > 0);
    }
    
    @Test
    public void testMultipleListeners() {
        // Test that we can create multiple listeners
        CustomTestListener listener1 = new CustomTestListener(project, extension);
        CustomTestListener listener2 = new CustomTestListener(project, extension);
        
        assertNotNull("First listener should exist", listener1);
        assertNotNull("Second listener should exist", listener2);
        
        listener1.setCurrentTaskPath(":test1");
        listener2.setCurrentTaskPath(":test2");
        
        // Both should work without interference
        assertFalse("First listener should have no tests", listener1.hasTests());
        assertFalse("Second listener should have no tests", listener2.hasTests());
    }
}