package io.github.clojang.gradldromus;

import java.io.IOException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.TestDescriptor;
import org.gradle.api.tasks.testing.TestListener;
import org.gradle.api.tasks.testing.TestResult;
import org.gradle.api.tasks.testing.logging.TestLoggingContainer;
import org.gradle.api.tasks.testing.logging.TestLogEvent;
import org.gradle.api.tasks.testing.logging.TestExceptionFormat;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.configuration.ConsoleOutput;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.clojang.gradldromus.AnsiColors.*;

public class GradlDromusPlugin implements Plugin<Project> {
    // Use a map to track listeners per root project
    private static final Map<Project, CustomTestListener> listeners = new ConcurrentHashMap<>();

    @Override
    public void apply(Project project) {
        // Create extension for configuration
        GradlDromusExtension extension = project.getExtensions()
            .create("gradldromus", GradlDromusExtension.class);
        AnsiColors colors = new AnsiColors(extension.isUseColors());
        CleanTerminalPrinter printer = new CleanTerminalPrinter(extension);
        Project rootProject = project.getRootProject();
        PrintStream output = System.out;
        
        // Get or create the listener for this root project
        CustomTestListener listener = listeners.computeIfAbsent(rootProject, p -> {
            CustomTestListener newListener = new CustomTestListener(p, extension);

            // Add a projects evaluated listener to print a greeting message
            p.getGradle().projectsEvaluated(result -> {
                // Write a greeting message
                Properties props = new Properties();
                printer.println(output, "\n" + colors.colorize("=".repeat(78), BRIGHT_GREEN));
                
                // Try multiple locations for the properties file
                String[] possiblePaths = {
                    "/io/github/clojang/gradldromus/plugin.properties",
                    "/plugin.properties",
                    "plugin.properties"
                };

                boolean loaded = false;
                for (String path : possiblePaths) {
                    try (InputStream in = getClass().getResourceAsStream(path)) {
                        if (in != null) {
                            props.load(in);
                            String pluginName = props.getProperty("plugin.name", "GradlDromus");
                            String pluginVersion = props.getProperty("plugin.version", "unknown");
                            printer.println(output, colors.colorize("Running tests with " + pluginName + " (version: " + pluginVersion + ")", GREEN));
                            //System.err.println("Running tests with " + pluginName + " (version: " + pluginVersion + ") [loaded from: " + path + "]");
                            loaded = true;
                            break;
                        }
                    } catch (IOException e) {
                        // Continue to next path
                    }
                }

                if (!loaded) {
                    // Fallback - get version from manifest or use default
                    Package pkg = getClass().getPackage();
                    String version = pkg != null ? pkg.getImplementationVersion() : "unknown";
                    printer.println(output, colors.colorize("Running tests with GradlDromus (version: " + version + ")", GREEN));
                    //System.err.println("Running tests with GradlDromus (version: " + version + ") [properties file not found]");
                }
                
                printer.println(output, colors.colorize("-".repeat(78), BRIGHT_GREEN));
            });

            // Add a build finished listener to print the final summary
            p.getGradle().buildFinished(result -> {
                CustomTestListener l = listeners.get(p);
                if (l != null && l.hasTests()) {
                    l.printFinalSummary();
                }
                listeners.remove(p); // Clean up
            });

            return newListener;
        });
        
        // Configure all test tasks in this project
        project.getTasks().withType(Test.class).configureEach(testTask -> {
            // Completely disable ALL default console output
            configureTestLogging(testTask);
            
            // Add our custom listener
            testTask.addTestListener(new TaskSpecificListener(listener, testTask));
            
            // Ensure test results are always generated
            testTask.getReports().getJunitXml().getRequired().set(true);
            
            // Try to make the task quieter
            testTask.doFirst(task -> {
                // Store original log level
                LogLevel originalLevel = project.getGradle().getStartParameter().getLogLevel();
                task.getExtensions().getExtraProperties().set("originalLogLevel", originalLevel);
                
                // Set to quiet during test execution
                if (extension.suppressGradleOutput) {
                    project.getGradle().getStartParameter().setLogLevel(LogLevel.QUIET);
                }
            });
            
            testTask.doLast(task -> {
                printer.println(output, "\n" + colors.colorize("-".repeat(78), BRIGHT_BLACK));
                // Restore original log level
                LogLevel originalLevel = (LogLevel) task.getExtensions().getExtraProperties().get("originalLogLevel");
                project.getGradle().getStartParameter().setLogLevel(originalLevel);
            });
        });
    }
    
    private void configureTestLogging(Test testTask) {
        TestLoggingContainer logging = testTask.getTestLogging();
        
        // Disable all events at all levels
        logging.setEvents(Collections.emptySet());
        logging.setShowStandardStreams(false);
        logging.setShowExceptions(false);
        logging.setShowCauses(false);
        logging.setShowStackTraces(false);
        
        // Disable for all log levels
        logging.getLifecycle().setEvents(Collections.emptySet());
        logging.getLifecycle().setExceptionFormat(TestExceptionFormat.FULL);
        logging.getQuiet().setEvents(Collections.emptySet());
        logging.getInfo().setEvents(Collections.emptySet());
        logging.getDebug().setEvents(Collections.emptySet());
        
        // Additional settings to suppress output
        logging.setDisplayGranularity(0);
        logging.setMinGranularity(0);
        logging.setMaxGranularity(0);
    }
    
    // Wrapper listener that sets the correct task path
    private static class TaskSpecificListener implements TestListener {
        private final CustomTestListener delegate;
        private final Test testTask;
        private boolean initialized = false;
        
        TaskSpecificListener(CustomTestListener delegate, Test testTask) {
            this.delegate = delegate;
            this.testTask = testTask;
        }
        
        private void ensureInitialized() {
            if (!initialized) {
                delegate.setCurrentTaskPath(testTask.getPath());
                initialized = true;
            }
        }
        
        @Override
        public void beforeSuite(TestDescriptor suite) {
            ensureInitialized();
            delegate.beforeSuite(suite);
        }
        
        @Override
        public void afterSuite(TestDescriptor suite, TestResult result) {
            delegate.afterSuite(suite, result);
        }
        
        @Override
        public void beforeTest(TestDescriptor testDescriptor) {
            ensureInitialized();
            delegate.beforeTest(testDescriptor);
        }
        
        @Override
        public void afterTest(TestDescriptor testDescriptor, TestResult result) {
            delegate.afterTest(testDescriptor, result);
        }
    }
}