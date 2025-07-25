package io.github.clojang.gradldromus;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.testing.Test;

public class GradlDromusPlugin implements Plugin<Project> {
    
    @Override
    public void apply(Project project) {
        // Create extension for configuration
        GradlDromusExtension extension = project.getExtensions()
            .create("gradldromus", GradlDromusExtension.class);
        
        // Configure all test tasks
        project.getTasks().withType(Test.class).configureEach(testTask -> {
            // Disable default console output
            testTask.getTestLogging().setShowStandardStreams(false);
            testTask.getTestLogging().setShowExceptions(false);
            testTask.getTestLogging().setShowCauses(false);
            testTask.getTestLogging().setShowStackTraces(false);
            
            // Add our custom test listener
            testTask.addTestListener(new CustomTestListener(project, extension));
            
            // Ensure test results are always generated (using new API)
            testTask.getReports().getJunitXml().getRequired().set(true);
        });
    }
}
