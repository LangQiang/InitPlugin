package com.lq.plugin.init;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;

public class InitPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        File file = new File("init.config");
        if (file.exists()) {
            file.delete();
        }
        System.out.println("\033[0;34m INIT Plugin Attach \033[0m");
        System.out.println("\033[0;34m " + project.getName() +" \033[0m");

        project.getExtensions().findByType(AppExtension.class).registerTransform(new InitTransform(project));

    }
}
