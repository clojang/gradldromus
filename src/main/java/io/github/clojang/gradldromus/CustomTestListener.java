package io.github.clojang.gradldromus;

import org.gradle.api.Project;
import org.gradle.api.tasks.testing.*;

import java.util.*;

public class CustomTestListener implements TestListener {
    private final Project project;
    private final GradlDromusExtension extension;
    private final Map<String, ModuleResults> moduleResults = new LinkedHashMap<>();
    private long suiteStartTime;
    
    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String GRAY = "\u001B[90m";
    
    public CustomTestListener(Project project, GradlDromusExtension extension) {
        this.project = project;
        this.extension = extension;
    }
    
    @Override
    public void beforeSuite(TestDescriptor suite) {
        if (suite.getParent() == null) {
            suiteStartTime = System.currentTimeMillis();
            println("\n" + color(BLUE, "Running tests...") + "\n");
        }
    }
    
    @Override
    public void afterSuite(TestDescriptor suite, TestResult result) {
        if (suite.getParent() == null) {
            // Print final summary
            printSummary(result);
        }
    }
    
    @Override
    public void beforeTest(TestDescriptor testDescriptor) {
        // Track module if needed
        String className = testDescriptor.getClassName();
        if (className != null && extension.isShowModuleNames()) {
            moduleResults.computeIfAbsent(className, k -> new ModuleResults(k));
        }
    }
    
    @Override
    public void afterTest(TestDescriptor testDescriptor, TestResult result) {
        String className = testDescriptor.getClassName();
        String methodName = testDescriptor.getName();
        
        if (className != null) {
            ModuleResults module = moduleResults.get(className);
            if (module != null) {
                module.addTest(methodName, result);
            }
        }
        
        // Print immediate feedback
        printTestResult(className, methodName, result);
    }
    
    private void printTestResult(String className, String methodName, TestResult result) {
        StringBuilder output = new StringBuilder();
        
        // Status symbol
        String symbol;
        String symbolColor;
        switch (result.getResultType()) {
            case SUCCESS:
                symbol = extension.getPassSymbol();
                symbolColor = GREEN;
                break;
            case FAILURE:
                symbol = extension.getFailSymbol();
                symbolColor = RED;
                break;
            case SKIPPED:
                symbol = extension.getSkipSymbol();
                symbolColor = YELLOW;
                break;
            default:
                symbol = "?";
                symbolColor = GRAY;
        }
        
        output.append(color(symbolColor, symbol)).append(" ");
        
        // Module/Class name
        if (extension.isShowModuleNames() && className != null) {
            String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
            output.append(color(GRAY, simpleClassName)).append(".");
        }
        
        // Method name
        if (extension.isShowMethodNames()) {
            output.append(methodName);
        }
        
        // Timing
        if (extension.isShowTimings()) {
            long duration = result.getEndTime() - result.getStartTime();
            output.append(color(GRAY, " (" + duration + "ms)"));
        }
        
        println(output.toString());
        
        // Print failure details
        if (result.getResultType() == TestResult.ResultType.FAILURE) {
            for (Throwable exception : result.getExceptions()) {
                println(color(RED, "  → " + exception.getMessage()));
            }
        }
    }
    
    private void printSummary(TestResult result) {
        println("\n" + color(BLUE, "Test Summary:"));
        println(color(BLUE, "─────────────"));
        
        long totalTime = System.currentTimeMillis() - suiteStartTime;
        
        String summaryColor = result.getResultType() == TestResult.ResultType.SUCCESS ? GREEN : RED;
        
        println(String.format("%s %d tests, %s %d passed, %s %d failed, %s %d skipped",
            color(GRAY, "Total:"),
            result.getTestCount(),
            color(GREEN, extension.getPassSymbol()),
            result.getSuccessfulTestCount(),
            color(RED, extension.getFailSymbol()),
            result.getFailedTestCount(),
            color(YELLOW, extension.getSkipSymbol()),
            result.getSkippedTestCount()
        ));
        
        println(color(GRAY, "Time: ") + (totalTime / 1000.0) + "s");
        
        if (result.getResultType() == TestResult.ResultType.SUCCESS) {
            println("\n" + color(GREEN, "✨ All tests passed!"));
        } else {
            println("\n" + color(RED, "❌ Some tests failed."));
        }
    }
    
    private String color(String ansiColor, String text) {
        if (extension.isUseColors()) {
            return ansiColor + text + RESET;
        }
        return text;
    }
    
    private void println(String message) {
        System.out.println(message);
    }
    
    // Inner class to track module results
    private static class ModuleResults {
        private final String moduleName;
        private final List<TestInfo> tests = new ArrayList<>();
        
        ModuleResults(String moduleName) {
            this.moduleName = moduleName;
        }
        
        void addTest(String methodName, TestResult result) {
            tests.add(new TestInfo(methodName, result));
        }
    }
    
    private static class TestInfo {
        final String methodName;
        final TestResult result;
        
        TestInfo(String methodName, TestResult result) {
            this.methodName = methodName;
            this.result = result;
        }
    }
}
