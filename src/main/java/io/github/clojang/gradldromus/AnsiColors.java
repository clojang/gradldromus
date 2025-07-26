package io.github.clojang.gradldromus;

/**
 * Utility class for handling ANSI color codes and terminal text formatting.
 * Provides constants for various colors and methods for colorizing text output.
 */
public class AnsiColors {
    // ANSI escape codes
    
    /** ANSI reset code to clear all formatting */
    public static final String RESET = "\u001B[0m";
    
    /** ANSI bold text formatting code */
    public static final String BOLD = "\u001B[1m";
    
    // Regular colors
    //public static final String BLACK = "\u001B[30m";
    
    /** ANSI red color code */
    public static final String RED = "\u001B[31m";
    
    /** ANSI green color code */
    public static final String GREEN = "\u001B[32m";
    
    /** ANSI yellow color code */
    public static final String YELLOW = "\u001B[33m";
    
    /** ANSI blue color code */
    public static final String BLUE = "\u001B[34m";
    
    //public static final String MAGENTA = "\u001B[35m";
    
    /** ANSI cyan color code */
    public static final String CYAN = "\u001B[36m";
    
    /** ANSI white color code */
    public static final String WHITE = "\u001B[37m";
    
    // Bright colors
    
    /** ANSI bright black (gray) color code */
    public static final String BRIGHT_BLACK = "\u001B[90m";
    
    /** ANSI bright red color code */
    public static final String BRIGHT_RED = "\u001B[91m";
    
    /** ANSI bright green color code */
    public static final String BRIGHT_GREEN = "\u001B[92m";
    
    /** ANSI bright yellow color code */
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    
    //public static final String BRIGHT_BLUE = "\u001B[94m";
    //public static final String BRIGHT_MAGENTA = "\u001B[95m";
    
    /** ANSI bright cyan color code */
    public static final String BRIGHT_CYAN = "\u001B[96m";
    
    //public static final String BRIGHT_WHITE = "\u001B[97m";
    
    private final boolean useColors;
    
    /**
     * Creates a new AnsiColors instance.
     * 
     * @param useColors whether to apply colors or return plain text
     */
    public AnsiColors(boolean useColors) {
        this.useColors = useColors;
    }
    
    /**
     * Applies the specified ANSI color codes to the given text.
     * If colors are disabled, returns the original text unchanged.
     * 
     * @param text the text to colorize
     * @param colors the ANSI color codes to apply
     * @return the colorized text, or original text if colors are disabled or text is null
     */
    public String colorize(String text, String... colors) {
        if (!useColors || text == null) {
            return text;
        }
        
        StringBuilder sb = new StringBuilder();
        for (String color : colors) {
            sb.append(color);
        }
        sb.append(text).append(RESET);
        return sb.toString();
    }
    
    /**
     * Removes all ANSI escape codes from the given text.
     * 
     * @param text the text to strip ANSI codes from
     * @return the text with all ANSI codes removed, or null if input is null
     */
    public static String stripAnsi(String text) {
        if (text == null) {
            return null;
        }
        return text.replaceAll("\u001B\\[[;\\d]*m", "");
    }
}
