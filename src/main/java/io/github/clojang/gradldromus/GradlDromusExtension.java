package io.github.clojang.gradldromus;

public class GradlDromusExtension {
    public static final int MAX_STACK_TRACE_DEPTH = 10;
    // Public fields for Kotlin DSL compatibility
    public boolean showModuleNames = true;
    public boolean showMethodNames = true;
    public boolean showTimings = true;
    public boolean useColors = true;
    public boolean showStandardStreams = false;
    public boolean suppressGradleOutput = false;
    public int terminalWidth = CleanTerminalPrinter.DEFAULT_TERM_SM_WIDTH;
    public String passSymbol = "💚";
    public String failSymbol = "💔";
    public String skipSymbol = "💤";
    
    // New exception and stack trace options
    public boolean showExceptions = true;        // Show exception messages
    public boolean showStackTraces = false;     // Show stack traces (short form)
    public boolean showFullStackTraces = false; // Show full stack traces with all frames
    public int maxStackTraceDepth = MAX_STACK_TRACE_DEPTH;
    
    // Keep getters for Java compatibility and internal use
    public boolean isShowModuleNames() {
        return showModuleNames;
    }

    public boolean isShowMethodNames() {
        return showMethodNames;
    }

    public boolean isShowTimings() {
        return showTimings;
    }
    
    public void setShowTimings(boolean showTimings) {
        this.showTimings = showTimings;
    }
    
    public boolean isUseColors() {
        return useColors;
    }
    
    public void setUseColors(boolean useColors) {
        this.useColors = useColors;
    }
    
    public boolean isShowStandardStreams() {
        return showStandardStreams;
    }
    
    public boolean isSuppressGradleOutput() {
        return suppressGradleOutput;
    }

    public void setTerminalWidth(int terminalWidth) {
        this.terminalWidth = terminalWidth;
    }

    public int getTerminalWidth() {
        return terminalWidth;
    }

    public String getPassSymbol() {
        return passSymbol;
    }
    
    public void setPassSymbol(String passSymbol) {
        this.passSymbol = passSymbol;
    }
    
    public String getFailSymbol() {
        return failSymbol;
    }
    
    public void setFailSymbol(String failSymbol) {
        this.failSymbol = failSymbol;
    }
    
    public String getSkipSymbol() {
        return skipSymbol;
    }
    
    public void setSkipSymbol(String skipSymbol) {
        this.skipSymbol = skipSymbol;
    }
    
    // New getters and setters for exception handling
    public boolean isShowExceptions() {
        return showExceptions;
    }
    
    public void setShowExceptions(boolean showExceptions) {
        this.showExceptions = showExceptions;
    }
    
    public boolean isShowStackTraces() {
        return showStackTraces;
    }
    
    public void setShowStackTraces(boolean showStackTraces) {
        this.showStackTraces = showStackTraces;
    }
    
    public boolean isShowFullStackTraces() {
        return showFullStackTraces;
    }
    
    public void setShowFullStackTraces(boolean showFullStackTraces) {
        this.showFullStackTraces = showFullStackTraces;
    }
    
    public int getMaxStackTraceDepth() {
        return maxStackTraceDepth;
    }
    
    public void setMaxStackTraceDepth(int maxStackTraceDepth) {
        this.maxStackTraceDepth = maxStackTraceDepth;
    }
}