package io.github.clojang.gradldromus;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class CleanTerminalPrinterTest {
    
    private GradlDromusExtension extension;
    private CleanTerminalPrinter printer;
    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;
    
    @Before
    public void setUp() {
        extension = new GradlDromusExtension();
        printer = new CleanTerminalPrinter(extension);
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
    }
    
    @Test
    public void testPrint() {
        printer.print(printStream, "Hello World");
        String output = outputStream.toString();
        
        // Should contain the text (exact format depends on terminal clearing)
        assertTrue("Output should contain the text", output.contains("Hello World"));
    }
    
    @Test
    public void testPrintln() {
        printer.println(printStream, "Hello World");
        String output = outputStream.toString();
        
        // Should contain the text and end with newline
        assertTrue("Output should contain the text", output.contains("Hello World"));
        assertTrue("Output should end with newline", output.endsWith("\n"));
    }
    
    @Test
    public void testGetTerminalWidthDefault() {
        int width = printer.getTerminalWidth();
        assertTrue("Terminal width should be positive", width > 0);
        // Default should be 80 if no other source is available
        assertEquals("Default width should be 80", CleanTerminalPrinter.DEFAULT_TERM_SM_WIDTH, width);
    }
    
    @Test
    public void testGetTerminalWidthFromExtension() {
        extension.setTerminalWidth(CleanTerminalPrinter.DEFAULT_TERM_LG_WIDTH);
        int width = printer.getTerminalWidth();
        assertEquals("Width should match extension setting", CleanTerminalPrinter.DEFAULT_TERM_LG_WIDTH, width);
    }
    
    @Test
    public void testClearLine() {
        printer.clearLine(printStream);
        String output = outputStream.toString();
        
        // Should contain carriage return and spaces
        assertTrue("Output should contain carriage return", output.contains("\r"));
        assertTrue("Output should contain spaces for clearing", output.contains(" "));
    }
    
    @Test
    public void testMoveToLineStart() {
        printer.moveToLineStart(printStream);
        String output = outputStream.toString();
        
        assertEquals("Should output carriage return", "\r", output);
    }
}