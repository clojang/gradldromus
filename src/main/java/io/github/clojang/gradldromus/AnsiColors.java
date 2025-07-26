package io.github.clojang.gradldromus;

public class AnsiColors {
    // ANSI escape codes
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    
    // Regular colors
    //public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    //public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    
    // Bright colors
    public static final String BRIGHT_BLACK = "\u001B[90m";
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    //public static final String BRIGHT_BLUE = "\u001B[94m";
    //public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    //public static final String BRIGHT_WHITE = "\u001B[97m";
    
    private final boolean useColors;
    
    public AnsiColors(boolean useColors) {
        this.useColors = useColors;
    }
    
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
    
    public static String stripAnsi(String text) {
        if (text == null) {
            return null;
        }
        return text.replaceAll("\u001B\\[[;\\d]*m", "");
    }
}
