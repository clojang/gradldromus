package io.github.clojang.gradldromus;

import org.gradle.api.tasks.testing.TestListener;
import org.gradle.api.tasks.testing.TestDescriptor;
import org.gradle.api.tasks.testing.TestResult;

import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.PrintWriter;

import static io.github.clojang.gradldromus.AnsiColors.BOLD;
import static io.github.clojang.gradldromus.AnsiColors.BRIGHT_YELLOW;
import static io.github.clojang.gradldromus.AnsiColors.WHITE;
import static io.github.clojang.gradldromus.AnsiColors.YELLOW;
import static io.github.clojang.gradldromus.AnsiColors.BRIGHT_BLACK;
import static io.github.clojang.gradldromus.AnsiColors.BRIGHT_GREEN;
import static io.github.clojang.gradldromus.AnsiColors.BRIGHT_RED;
import static io.github.clojang.gradldromus.AnsiColors.BRIGHT_CYAN;
import static io.github.clojang.gradldromus.AnsiColors.RED;
import static io.github.clojang.gradldromus.AnsiColors.BLUE;
import static io.github.clojang.gradldromus.AnsiColors.GREEN;
import static io.github.clojang.gradldromus.AnsiColors.CYAN;

public class CustomTestListener implements TestListener {
    private static final double MILLISECONDS = 1000.0;
    private static final int DOTS_PAD = 76;
    private final GradlDromusExtension extension;
    private final AnsiColors colors;
    private final CleanTerminalPrinter printer;
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
    
    public CustomTestListener(GradlDromusExtension extension) {
        this.extension = extension;
        this.colors = new AnsiColors(extension.isUseColors());
        this.printer = new CleanTerminalPrinter(extension);
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
            printer.println(output, colors.colorize(taskPath, BOLD, BRIGHT_YELLOW));
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
        int dotsNeeded = Math.max(1, DOTS_PAD - nameLength); // this tends to give most results in under 80 characters
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
        
        outputStr.append(colors.colorize(symbol, symbolColor));
        
        // Timing (dark gray)
        if (extension.isShowTimings()) {
            long duration = result.getEndTime() - result.getStartTime();
            outputStr.append(" ").append(colors.colorize("(" + duration + "ms)", BRIGHT_BLACK));
        }
        
        // Print the test result using clean printer
        printer.println(output, outputStr.toString());
        
        // Print failure details if needed and configured
        if (result.getResultType() == TestResult.ResultType.FAILURE) {
            printFailureDetails(result);
        }
    }
    
    private void printFailureDetails(TestResult result) {
        List<Throwable> exceptions = result.getExceptions();
        if (exceptions.isEmpty()) {
            return;
        }
        
        for (Throwable exception : exceptions) {
            // Always show the exception message if showExceptions is true (default)
            if (extension.isShowExceptions()) {
                String message = getExceptionMessage(exception);
                if (message != null && !message.trim().isEmpty()) {
                    printer.println(output, colors.colorize("    → " + message, RED));
                }
            }
            
            // Show stack traces if requested
            if (extension.isShowStackTraces() || extension.isShowFullStackTraces()) {
                printStackTrace(exception);
            }
        }
    }
    
    private String getExceptionMessage(Throwable exception) {
        String message = exception.getMessage();
        if (message == null || message.trim().isEmpty()) {
            // If no message, use the exception class name
            return exception.getClass().getSimpleName();
        }
        return message;
    }
    
    private void printStackTrace(Throwable exception) {
        if (extension.isShowFullStackTraces()) {
            // Show complete stack trace
            printFullStackTrace(exception);
        } else if (extension.isShowStackTraces()) {
            // Show limited stack trace
            printLimitedStackTrace(exception);
        }
    }
    
    private void printFullStackTrace(Throwable exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String fullTrace = sw.toString();
        
        String[] lines = fullTrace.split("\\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                printer.println(output, colors.colorize("      " + line.trim(), RED));
            }
        }
    }
    
    private void printLimitedStackTrace(Throwable exception) {
        StackTraceElement[] stackTrace = exception.getStackTrace();
        if (stackTrace.length == 0) {
            return;
        }
        
        // Print the exception class and message
        printer.println(output, colors.colorize("      " + exception.getClass().getName() + 
            (exception.getMessage() != null ? ": " + exception.getMessage() : ""), RED));
        
        // Print limited number of stack trace elements
        int limit = Math.min(stackTrace.length, extension.getMaxStackTraceDepth());
        for (int i = 0; i < limit; i++) {
            StackTraceElement element = stackTrace[i];
            String stackLine = String.format("        at %s.%s(%s:%d)",
                element.getClassName(),
                element.getMethodName(),
                element.getFileName() != null ? element.getFileName() : "Unknown Source",
                element.getLineNumber());
            printer.println(output, colors.colorize(stackLine, RED));
        }
        
        // Show "... X more" if there are more stack trace elements
        if (stackTrace.length > limit) {
            int remaining = stackTrace.length - limit;
            printer.println(output, colors.colorize("        ... " + remaining + " more", RED));
        }
        
        // Handle caused by exceptions
        Throwable cause = exception.getCause();
        if (cause != null && cause != exception) {
            printer.println(output, colors.colorize("      Caused by: ", RED));
            printLimitedStackTrace(cause);
        }
    }

    public void printFinalSummary() {
        long totalTime = System.currentTimeMillis() - globalStartTime.get();
        
        printer.println(output, "\n" + colors.colorize("Test Summary:", BLUE));
        printer.println(output, colors.colorize("─────────────", BLUE));
        
        StringBuilder summary = new StringBuilder();
        summary.append(colors.colorize("Total: " + totalTests.get() + " tests, ", WHITE));
        summary.append(colors.colorize(extension.getPassSymbol() + " " + totalPassed.get() + " passed, ", GREEN));
        summary.append(colors.colorize(extension.getFailSymbol() + " " + totalFailed.get() + " failed, ", RED));
        summary.append(colors.colorize(extension.getSkipSymbol() + " " + totalSkipped.get() + " skipped", CYAN));
        
        printer.println(output, summary.toString());
        
        printer.println(output, colors.colorize("Time: ", WHITE) + (totalTime / MILLISECONDS) + "s");
        
        if (totalFailed.get() == 0) {
            printer.println(output, "\n" + colors.colorize("✨ All tests passed!", BRIGHT_GREEN));
        } else {
            printer.println(output, "\n" + colors.colorize("❌ Some tests failed.", BRIGHT_RED));
        }
        printer.println(output, "");
        printer.printHeading(output, colors, "=", BRIGHT_GREEN);
    }
}