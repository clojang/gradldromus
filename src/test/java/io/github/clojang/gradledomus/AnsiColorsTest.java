package io.github.clojang.gradldromus;

import org.junit.Test;
import static org.junit.Assert.*;

public class AnsiColorsTest {
    
    @Test
    public void testColorizeWithColors() {
        AnsiColors colors = new AnsiColors(true);
        String result = colors.colorize("test", AnsiColors.RED, AnsiColors.BOLD);
        
        assertEquals(AnsiColors.RED + AnsiColors.BOLD + "test" + AnsiColors.RESET, result);
    }
    
    @Test
    public void testColorizeWithoutColors() {
        AnsiColors colors = new AnsiColors(false);
        String result = colors.colorize("test", AnsiColors.RED, AnsiColors.BOLD);
        
        assertEquals("test", result);
    }
    
    @Test
    public void testColorizeNullText() {
        AnsiColors colors = new AnsiColors(true);
        String result = colors.colorize(null, AnsiColors.RED);
        
        assertNull(result);
    }
    
    @Test
    public void testStripAnsi() {
        String textWithAnsi = AnsiColors.RED + "Hello" + AnsiColors.BOLD + "World" + AnsiColors.RESET;
        String result = AnsiColors.stripAnsi(textWithAnsi);
        
        assertEquals("HelloWorld", result);
    }
    
    @Test
    public void testStripAnsiNull() {
        String result = AnsiColors.stripAnsi(null);
        assertNull(result);
    }
    
    @Test
    public void testStripAnsiPlainText() {
        String plainText = "Hello World";
        String result = AnsiColors.stripAnsi(plainText);
        
        assertEquals(plainText, result);
    }
    
    @Test
    public void testAnsiConstants() {
        // Verify ANSI codes are correct
        assertEquals("\u001B[0m", AnsiColors.RESET);
        assertEquals("\u001B[1m", AnsiColors.BOLD);
        assertEquals("\u001B[31m", AnsiColors.RED);
        assertEquals("\u001B[32m", AnsiColors.GREEN);
        assertEquals("\u001B[91m", AnsiColors.BRIGHT_RED);
        assertEquals("\u001B[92m", AnsiColors.BRIGHT_GREEN);
    }
}