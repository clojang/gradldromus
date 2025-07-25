package io.github.clojang.gradldromus;

import org.gradle.api.Project;
import org.gradle.api.tasks.testing.*;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.io.PrintStream;

import static io.github.clojang.gradldromus.AnsiColors.*;

public class CustomTestListener implements TestListener {
    private final Project project;
    private final GradlDromusExtension extension;
    private final AnsiColors colors;
    private final PrintStream output;
    
    // Thread-safe tracking of current task paths
    private final Map<String, Boolean> taskHeadersPrinted = new ConcurrentHashMap<>();
    private final ThreadLocal<String> currentTaskPath = new ThreadLocal<>();
    
    // Global statistics (thread-safe)
    private final AtomicLong globalStartTime = new AtomicLong(0);
    private final AtomicInteger totalTests = new AtomicInteger(0);
    private final AtomicInteger totalPassed = new AtomicInteger(0);
    private final AtomicInteger totalFailed = new AtomicInteger(0);
    private final AtomicInteger totalSkipped = new AtomicInteger(0);
    
    public CustomTestListener(Project project, GradlDromusExtension extension) {
        this.project = project;
        this.extension = extension;
        this.colors = new AnsiColors(extension.isUseColors());
        // Always use System.out directly to bypass Gradle's logging
        this.output = System.out;
    }
    
    public boolean hasTests() {
        return totalTests.get() > 0;
    }
    
    public void setCurrentTaskPath(String taskPath) {
        currentTaskPath.set(taskPath);
    }
    
    @Override
    public void beforeSuite(TestDescriptor suite) {
        if (suite.getParent() == null) {
            // This is the root test suite
            globalStartTime.compareAndSet(0, System.currentTimeMillis());
        }
    }
    
    @Override
    public void afterSuite(TestDescriptor suite, TestResult result) {
        // No per-suite summaries
    }
    
    @Override
    public void beforeTest(TestDescriptor testDescriptor) {
        String taskPath = currentTaskPath.get();
        
        // Print the module header if not already printed for this task
        if (taskPath != null && taskHeadersPrinted.putIfAbsent(taskPath, Boolean.TRUE) == null) {
            synchronized (output) {
                output.println(colors.colorize(taskPath, BOLD, BRIGHT_YELLOW));
            }
        }
    }
    
    @Override
    public void afterTest(TestDescriptor testDescriptor, TestResult result) {
        String className = testDescriptor.getClassName();
        String methodName = testDescriptor.getName();
        
        // Update global totals
        totalTests.incrementAndGet();
        switch (result.getResultType()) {
            case SUCCESS:
                totalPassed.incrementAndGet();
                break;
            case FAILURE:
                totalFailed.incrementAndGet();
                break;
            case SKIPPED:
                totalSkipped.incrementAndGet();
                break;
        }
        
        // Format and print the test result
        printTestResult(className, methodName, result);
    }
    
    private void printTestResult(String className, String methodName, TestResult result) {
        StringBuilder outputStr = new StringBuilder();
        
        // Indent
        outputStr.append("    ");
        
        // Class name (light gray/white)
        if (className != null) {
            String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
            outputStr.append(colors.colorize(simpleClassName + ".", WHITE));
        }
        
        // Method name (light gray/white)
        outputStr.append(colors.colorize(methodName + " ", YELLOW));
        
        // Calculate dots needed
        int nameLength = 2; // indent
        if (className != null) {
            nameLength += className.substring(className.lastIndexOf('.') + 1).length() + 1;
        }
        nameLength += methodName.length() + 1;
        int dotsNeeded = Math.max(1, 74 - nameLength); // this tends to give most results in under 80 characters
        outputStr.append(colors.colorize(".".repeat(dotsNeeded), BRIGHT_BLACK));
        
        // Status in brackets
        String symbol;
        String symbolColor;
        switch (result.getResultType()) {
            case SUCCESS:
                symbol = extension.getPassSymbol();
                symbolColor = BOLD + BRIGHT_GREEN;
                break;
            case FAILURE:
                symbol = extension.getFailSymbol();
                symbolColor = BOLD + BRIGHT_RED;
                break;
            case SKIPPED:
                symbol = extension.getSkipSymbol();
                symbolColor = BOLD + BRIGHT_CYAN;
                break;
            default:
                symbol = "?";
                symbolColor = YELLOW;
        }
        
        //outputStr.append(colors.colorize("[", WHITE));
        outputStr.append(colors.colorize(symbol, symbolColor));
        //outputStr.append(colors.colorize("]", WHITE));
        
        // Timing (dark gray)
        if (extension.isShowTimings()) {
            long duration = result.getEndTime() - result.getStartTime();
            outputStr.append(" ").append(colors.colorize("(" + duration + "ms)", BRIGHT_BLACK));
        }
        
        // Print with synchronization to avoid interleaving
        synchronized (output) {
            output.print("\r"); // Move to start of line
            output.print(" ".repeat(getTerminalWidth())); // Overwrite with spaces (adjust length as needed)
            output.print("\r"); // Move to start again
            output.println(outputStr);
            
            // Print failure details on the next line(s) if needed
            if (result.getResultType() == TestResult.ResultType.FAILURE) {
                for (Throwable exception : result.getExceptions()) {
                    output.println(colors.colorize("    → " + exception.getMessage(), RED));
                }
            }
        }
    }

    public int getTerminalWidth() {
        // Check extension property first (if available)
        int configuredWidth = this.extension.getTerminalWidth();
        if (configuredWidth > 0) {
            //System.err.println("Detected terminal width from extension: " + configuredWidth);
            return configuredWidth;
        }
        // Try environment variable
        String columnsEnv = System.getenv("COLUMNS");
        if (columnsEnv != null) {
            try {
                int width = Integer.parseInt(columnsEnv.trim());
                //System.err.println("Detected terminal width from COLUMNS: " + width);
                return width;
            } catch (NumberFormatException ignored) {}
        }
        // Try tput
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", "tput cols"});
            process.waitFor();
            Scanner scanner = new Scanner(process.getInputStream());
            if (scanner.hasNext()) {
                String colsStr = scanner.next().trim();
                //int width = Integer.parseInt(colsStr);
                //System.err.println("Detected terminal width from 'tput cols': " + width);
                //return width;
                return Integer.parseInt(colsStr);}
        } catch (Exception e) {
            Logger logger = Logging.getLogger(CustomTestListener.class);
            logger.warn("Could not determine terminal width, using default: 80", e);
        }
        return 80; // Default width for overwriting text
    }

    public void printFinalSummary() {
        long totalTime = System.currentTimeMillis() - globalStartTime.get();
        
        synchronized (output) {
            output.println("\n" + colors.colorize("Test Summary:", BLUE));
            output.println(colors.colorize("─────────────", BLUE));
            
            StringBuilder summary = new StringBuilder();
            summary.append(colors.colorize("Total: " + totalTests.get() + " tests, ", WHITE));
            summary.append(colors.colorize(extension.getPassSymbol() + " " + totalPassed.get() + " passed, ", GREEN));
            summary.append(colors.colorize(extension.getFailSymbol() + " " + totalFailed.get() + " failed, ", RED));
            summary.append(colors.colorize(extension.getSkipSymbol() + " " + totalSkipped.get() + " skipped", CYAN));
            
            output.println(summary.toString());
            
            output.println(colors.colorize("Time: ", WHITE) + (totalTime / 1000.0) + "s");
            
            if (totalFailed.get() == 0) {
                output.println("\n" + colors.colorize("✨ All tests passed!", BRIGHT_GREEN));
            } else {
                output.println("\n" + colors.colorize("❌ Some tests failed.", BRIGHT_RED));
            }

            output.println(colors.colorize("\n" + "=".repeat(78), BRIGHT_GREEN) + "\n");
        }
    }
}
