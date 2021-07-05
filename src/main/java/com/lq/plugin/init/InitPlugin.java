package com.lq.plugin.init;

import com.android.build.gradle.AppExtension;
import com.lq.plugin.init.transform.InitTransform;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class InitPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {
        Log.e("INIT Plugin Attach");
        AppExtension appExtension = project.getExtensions().findByType(AppExtension.class);
        if (appExtension != null) {
            appExtension.registerTransform(new InitTransform(project));
        } else {
            Log.e("appExtension is null");
        }
    }
}
