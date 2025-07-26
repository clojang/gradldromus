package io.github.clojang.gradldromus;

/**
 * Configuration extension for the GradlDromus plugin.
 * Provides settings for customizing test output formatting, colors, symbols, and behavior.
 */
public class GradlDromusExtension {
    /** Default maximum depth for stack trace output */
    public static final int MAX_STACK_TRACE_DEPTH = 10;
    
    // Public fields for Kotlin DSL compatibility
    
    /** Whether to show module names in test output */
    public boolean showModuleNames = true;
    
    /** Whether to show method names in test output */
    public boolean showMethodNames = true;
    
    /** Whether to show timing information for tests */
    public boolean showTimings = true;
    
    /** Whether to use ANSI colors in output */
    public boolean useColors = true;
    
    /** Whether to show standard output and error streams from tests */
    public boolean showStandardStreams = false;
    
    /** Whether to suppress Gradle's default output during test execution */
    public boolean suppressGradleOutput = false;
    
    /** Terminal width for formatting output */
    public int terminalWidth = CleanTerminalPrinter.DEFAULT_TERM_SM_WIDTH;
    
    /** Symbol to display for passed tests */
    public String passSymbol = "💚";
    
    /** Symbol to display for failed tests */
    public String failSymbol = "💔";
    
    /** Symbol to display for skipped tests */
    public String skipSymbol = "💤";
    
    // New exception and stack trace options
    
    /** Whether to show exception messages for failed tests */
    public boolean showExceptions = true;        // Show exception messages
    
    /** Whether to show stack traces in short form for failed tests */
    public boolean showStackTraces = false;     // Show stack traces (short form)
    
    /** Whether to show complete stack traces for failed tests */
    public boolean showFullStackTraces = false; // Show full stack traces with all frames
    
    /** Maximum depth for stack trace output when showing short stack traces */
    public int maxStackTraceDepth = MAX_STACK_TRACE_DEPTH;
    
    /**
     * Default constructor for the extension.
     */
    public GradlDromusExtension() {
        // Default constructor with default values already set above
    }
    
    // Keep getters for Java compatibility and internal use
    
    /**
     * Gets whether module names should be shown in test output.
     * 
     * @return true if module names should be shown
     */
    public boolean isShowModuleNames() {
        return showModuleNames;
    }

    /**
     * Gets whether method names should be shown in test output.
     * 
     * @return true if method names should be shown
     */
    public boolean isShowMethodNames() {
        return showMethodNames;
    }

    /**
     * Gets whether timing information should be shown for tests.
     * 
     * @return true if timing information should be shown
     */
    public boolean isShowTimings() {
        return showTimings;
    }
    
    /**
     * Sets whether timing information should be shown for tests.
     * 
     * @param showTimings true to show timing information
     */
    public void setShowTimings(boolean showTimings) {
        this.showTimings = showTimings;
    }
    
    /**
     * Gets whether ANSI colors should be used in output.
     * 
     * @return true if colors should be used
     */
    public boolean isUseColors() {
        return useColors;
    }
    
    /**
     * Sets whether ANSI colors should be used in output.
     * 
     * @param useColors true to use colors
     */
    public void setUseColors(boolean useColors) {
        this.useColors = useColors;
    }
    
    /**
     * Gets whether standard output and error streams from tests should be shown.
     * 
     * @return true if standard streams should be shown
     */
    public boolean isShowStandardStreams() {
        return showStandardStreams;
    }
    
    /**
     * Gets whether Gradle's default output should be suppressed during test execution.
     * 
     * @return true if Gradle output should be suppressed
     */
    public boolean isSuppressGradleOutput() {
        return suppressGradleOutput;
    }

    /**
     * Sets the terminal width for formatting output.
     * 
     * @param terminalWidth the terminal width in characters
     */
    public void setTerminalWidth(int terminalWidth) {
        this.terminalWidth = terminalWidth;
    }

    /**
     * Gets the terminal width for formatting output.
     * 
     * @return the terminal width in characters
     */
    public int getTerminalWidth() {
        return terminalWidth;
    }

    /**
     * Gets the symbol used for passed tests.
     * 
     * @return the pass symbol
     */
    public String getPassSymbol() {
        return passSymbol;
    }
    
    /**
     * Sets the symbol used for passed tests.
     * 
     * @param passSymbol the pass symbol
     */
    public void setPassSymbol(String passSymbol) {
        this.passSymbol = passSymbol;
    }
    
    /**
     * Gets the symbol used for failed tests.
     * 
     * @return the fail symbol
     */
    public String getFailSymbol() {
        return failSymbol;
    }
    
    /**
     * Sets the symbol used for failed tests.
     * 
     * @param failSymbol the fail symbol
     */
    public void setFailSymbol(String failSymbol) {
        this.failSymbol = failSymbol;
    }
    
    /**
     * Gets the symbol used for skipped tests.
     * 
     * @return the skip symbol
     */
    public String getSkipSymbol() {
        return skipSymbol;
    }
    
    /**
     * Sets the symbol used for skipped tests.
     * 
     * @param skipSymbol the skip symbol
     */
    public void setSkipSymbol(String skipSymbol) {
        this.skipSymbol = skipSymbol;
    }
    
    // New getters and setters for exception handling
    
    /**
     * Gets whether exception messages should be shown for failed tests.
     * 
     * @return true if exception messages should be shown
     */
    public boolean isShowExceptions() {
        return showExceptions;
    }
    
    /**
     * Sets whether exception messages should be shown for failed tests.
     * 
     * @param showExceptions true to show exception messages
     */
    public void setShowExceptions(boolean showExceptions) {
        this.showExceptions = showExceptions;
    }
    
    /**
     * Gets whether stack traces should be shown in short form for failed tests.
     * 
     * @return true if short stack traces should be shown
     */
    public boolean isShowStackTraces() {
        return showStackTraces;
    }
    
    /**
     * Sets whether stack traces should be shown in short form for failed tests.
     * 
     * @param showStackTraces true to show short stack traces
     */
    public void setShowStackTraces(boolean showStackTraces) {
        this.showStackTraces = showStackTraces;
    }
    
    /**
     * Gets whether complete stack traces should be shown for failed tests.
     * 
     * @return true if full stack traces should be shown
     */
    public boolean isShowFullStackTraces() {
        return showFullStackTraces;
    }
    
    /**
     * Sets whether complete stack traces should be shown for failed tests.
     * 
     * @param showFullStackTraces true to show full stack traces
     */
    public void setShowFullStackTraces(boolean showFullStackTraces) {
        this.showFullStackTraces = showFullStackTraces;
    }
    
    /**
     * Gets the maximum depth for stack trace output when showing short stack traces.
     * 
     * @return the maximum stack trace depth
     */
    public int getMaxStackTraceDepth() {
        return maxStackTraceDepth;
    }
    
    /**
     * Sets the maximum depth for stack trace output when showing short stack traces.
     * 
     * @param maxStackTraceDepth the maximum stack trace depth
     */
    public void setMaxStackTraceDepth(int maxStackTraceDepth) {
        this.maxStackTraceDepth = maxStackTraceDepth;
    }
}
