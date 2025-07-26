package io.github.clojang.gradldromus;

import org.gradle.api.Project;
import org.gradle.api.tasks.testing.Test;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class GradlDromusPluginTest {
    public static final int SHORT_STACK = 5;
    @Rule
    public TemporaryFolder testProjectDir = new TemporaryFolder();
    
    private Project project;
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    
    @Before
    public void setUp() {
        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        ByteArrayOutputStream testErr = new ByteArrayOutputStream();
        project = ProjectBuilder.builder().build();
        
        // Capture output for testing
        System.setOut(new PrintStream(testOut));
        System.setErr(new PrintStream(testErr));
    }
    
    @After
    public void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    
    @org.junit.Test
    public void testPluginApplies() {
        project.getPluginManager().apply("io.github.clojang.gradldromus");
        
        // Verify extension is created
        GradlDromusExtension extension = project.getExtensions()
            .findByType(GradlDromusExtension.class);
        
        assert extension != null : "Extension should be created";
    }
    
    @org.junit.Test
    public void testDefaultExtensionValues() {
        project.getPluginManager().apply("io.github.clojang.gradldromus");
        
        GradlDromusExtension extension = project.getExtensions()
            .getByType(GradlDromusExtension.class);
        
        // Test default values
        assert extension.isShowModuleNames() : "showModuleNames should default to true";
        assert extension.isShowMethodNames() : "showMethodNames should default to true";
        assert extension.isShowTimings() : "showTimings should default to true";
        assert extension.isUseColors() : "useColors should default to true";
        assert !extension.isShowStandardStreams() : "showStandardStreams should default to false";
        assert !extension.isSuppressGradleOutput() : "suppressGradleOutput should default to false";
        
        // Test new exception options
        assert extension.isShowExceptions() : "showExceptions should default to true";
        assert !extension.isShowStackTraces() : "showStackTraces should default to false";
        assert !extension.isShowFullStackTraces() : "showFullStackTraces should default to false";
        assert extension.getMaxStackTraceDepth() == GradlDromusExtension.MAX_STACK_TRACE_DEPTH : "maxStackTraceDepth should default to 10";
        
        // Test symbols
        assert "💚".equals(extension.getPassSymbol()) : "passSymbol should default to 💚";
        assert "💔".equals(extension.getFailSymbol()) : "failSymbol should default to 💔";
        assert "💤".equals(extension.getSkipSymbol()) : "skipSymbol should default to 💤";
    }
    
    @org.junit.Test
    public void testExtensionConfiguration() {
        project.getPluginManager().apply("io.github.clojang.gradldromus");
        
        GradlDromusExtension extension = project.getExtensions()
            .getByType(GradlDromusExtension.class);
        
        // Configure extension
        extension.setShowExceptions(false);
        extension.setShowStackTraces(true);
        extension.setShowFullStackTraces(true);
        extension.setMaxStackTraceDepth(SHORT_STACK);
        extension.setPassSymbol("✅");
        extension.setFailSymbol("❌");
        extension.setSkipSymbol("⏭");
        extension.setTerminalWidth(CleanTerminalPrinter.DEFAULT_TERM_LG_WIDTH);
        
        // Verify configuration
        assert !extension.isShowExceptions() : "showExceptions should be false";
        assert extension.isShowStackTraces() : "showStackTraces should be true";
        assert extension.isShowFullStackTraces() : "showFullStackTraces should be true";
        assert extension.getMaxStackTraceDepth() == SHORT_STACK : "maxStackTraceDepth should be 5";
        assert "✅".equals(extension.getPassSymbol()) : "passSymbol should be ✅";
        assert "❌".equals(extension.getFailSymbol()) : "failSymbol should be ❌";
        assert "⏭".equals(extension.getSkipSymbol()) : "skipSymbol should be ⏭";
        assert extension.getTerminalWidth() == CleanTerminalPrinter.DEFAULT_TERM_LG_WIDTH : "terminalWidth should be 120";
    }
    
    @org.junit.Test
    public void testTestTaskConfiguration() {
        // Apply plugin
        project.getPluginManager().apply("java");
        project.getPluginManager().apply("io.github.clojang.gradldromus");
        
        // Create a test task
        Test testTask = project.getTasks().create("testExample", Test.class);
        
        // Verify the task exists and has been configured
        assert testTask != null : "Test task should be created";
        
        // Verify test logging is configured (events should be empty)
        assert testTask.getTestLogging().getEvents().isEmpty() : "Test events should be empty";
        assert !testTask.getTestLogging().getShowStandardStreams() : "ShowStandardStreams should be false";
        assert !testTask.getTestLogging().getShowExceptions() : "ShowExceptions should be false";
    }
}