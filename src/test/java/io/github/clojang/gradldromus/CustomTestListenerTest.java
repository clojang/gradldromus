package io.github.clojang.gradldromus;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class CustomTestListenerTest {
    public static final int SHORT_STACK = 3;
    private GradlDromusExtension extension;
    private CustomTestListener listener;
    private final PrintStream originalOut = System.out;
    
    @Before
    public void setUp() {
        extension = new GradlDromusExtension();
        listener = new CustomTestListener(extension);
        
        // Capture output
        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
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
        extension.setMaxStackTraceDepth(GradlDromusPluginTest.SHORT_STACK);
        
        CustomTestListener customListener = new CustomTestListener(extension);
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
        
        extension.setMaxStackTraceDepth(SHORT_STACK);
        assertEquals("maxStackTraceDepth should be 3", SHORT_STACK, extension.getMaxStackTraceDepth());
        
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
        extension.setTerminalWidth(CleanTerminalPrinter.DEFAULT_TERM_LG_WIDTH);
        assertEquals("Terminal width should be 120", CleanTerminalPrinter.DEFAULT_TERM_LG_WIDTH, printer.getTerminalWidth());
        
        extension.setTerminalWidth(0);
        assertTrue("Should use default width when 0", printer.getTerminalWidth() > 0);
    }
    
    @Test
    public void testMultipleListeners() {
        // Test that we can create multiple listeners
        CustomTestListener listener1 = new CustomTestListener(extension);
        CustomTestListener listener2 = new CustomTestListener(extension);
        
        assertNotNull("First listener should exist", listener1);
        assertNotNull("Second listener should exist", listener2);
        
        listener1.setCurrentTaskPath(":test1");
        listener2.setCurrentTaskPath(":test2");
        
        // Both should work without interference
        assertFalse("First listener should have no tests", listener1.hasTests());
        assertFalse("Second listener should have no tests", listener2.hasTests());
    }
}