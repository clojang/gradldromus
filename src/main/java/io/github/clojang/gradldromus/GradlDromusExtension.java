package io.github.clojang.gradldromus;

public class GradlDromusExtension {
    // Public fields for Kotlin DSL compatibility
    public boolean showModuleNames = true;
    public boolean showMethodNames = true;
    public boolean showTimings = true;
    public boolean useColors = true;
    public String passSymbol = "✓";
    public String failSymbol = "✗";
    public String skipSymbol = "○";
    
    // Keep getters for Java compatibility and internal use
    public boolean isShowModuleNames() {
        return showModuleNames;
    }
    
    public void setShowModuleNames(boolean showModuleNames) {
        this.showModuleNames = showModuleNames;
    }
    
    public boolean isShowMethodNames() {
        return showMethodNames;
    }
    
    public void setShowMethodNames(boolean showMethodNames) {
        this.showMethodNames = showMethodNames;
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
}
