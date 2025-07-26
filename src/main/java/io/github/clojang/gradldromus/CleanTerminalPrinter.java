package io.github.clojang.gradldromus;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Utility class for clean terminal output with line clearing and formatting capabilities.
 * Handles terminal width detection and provides methods for overwriting terminal content.
 */
public class CleanTerminalPrinter {
    /** Default terminal width for small terminals */
    public static final int DEFAULT_TERM_SM_WIDTH = 80;
    
    /** Default terminal width for large terminals */
    public static final int DEFAULT_TERM_LG_WIDTH = 120;

    private final GradlDromusExtension extension;
    private final Logger logger;
    
    /**
     * Creates a new CleanTerminalPrinter with the specified extension configuration.
     * 
     * @param extension the plugin extension containing configuration settings
     */
    public CleanTerminalPrinter(GradlDromusExtension extension) {
        this.extension = extension;
        this.logger = Logging.getLogger(CleanTerminalPrinter.class);
    }
    
    /**
     * Print a string to the specified output stream, overwriting any existing content on the current line
     * 
     * @param output the output stream to write to
     * @param text the text to print
     */
    public void print(PrintStream output, String text) {
        synchronized (output) {
            clearLine(output);
            output.print(text);
        }
    }
    
    /**
     * Print a string with newline to the specified output stream, overwriting any existing content on the current line
     * 
     * @param output the output stream to write to
     * @param text the text to print
     */
    public void println(PrintStream output, String text) {
        synchronized (output) {
            clearLine(output);
            output.println(text);
        }
    }

    /**
     * Prints a heading line using repeated characters in the specified color.
     * 
     * @param output the output stream to write to
     * @param colors the AnsiColors instance for colorization
     * @param chr the character to repeat for the heading
     * @param color the color to apply to the heading
     */
    public void printHeading(PrintStream output, AnsiColors colors, String chr, String color) {
        synchronized (output) {
            clearLine(output);
            output.println(colors.colorize(chr.repeat(DEFAULT_TERM_SM_WIDTH), color));
        }
    }
    
    /**
     * Determine the terminal width for proper line clearing
     * 
     * @return the terminal width in characters
     */
    public int getTerminalWidth() {
        // Check extension property first (if available)
        int configuredWidth = this.extension.getTerminalWidth();
        if (configuredWidth > 0) {
            return configuredWidth;
        }
        
        // Try environment variable
        String columnsEnv = System.getenv("COLUMNS");
        if (columnsEnv != null) {
            try {
                return Integer.parseInt(columnsEnv.trim());
            } catch (NumberFormatException ignored) {}
        }
        
        // Try tput
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", "tput cols"});
            process.waitFor();
            Scanner scanner = new Scanner(process.getInputStream());
            if (scanner.hasNext()) {
                String colsStr = scanner.next().trim();
                return Integer.parseInt(colsStr);
            }
        } catch (Exception e) {
            logger.warn(
                    "Could not determine terminal width, using default: {}",
                    DEFAULT_TERM_SM_WIDTH, e);
        }
        
        return DEFAULT_TERM_SM_WIDTH;
    }
    
    /**
     * Clear the current line completely
     * 
     * @param output the output stream to clear the line on
     */
    public void clearLine(PrintStream output) {
        synchronized (output) {
            moveToLineStart(output);
            output.print(" ".repeat(getTerminalWidth())); // Overwrite with spaces
            moveToLineStart(output);
        }
    }
    
    /**
     * Move to the start of the current line without clearing
     * 
     * @param output the output stream to move the cursor on
     */
    public void moveToLineStart(PrintStream output) {
        synchronized (output) {
            output.print("\r");
        }
    }
}
