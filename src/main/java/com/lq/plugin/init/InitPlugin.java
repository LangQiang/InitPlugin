package com.lq.plugin.init;

import com.android.build.gradle.AppExtension;
import com.lq.plugin.init.transform.InitTransform;
import com.lq.plugin.init.utils.ConfigFileMgr;
import com.lq.plugin.init.utils.Log;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InitPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {

        List<String> taskNames = project.getGradle().getStartParameter().getTaskNames();

        if (taskNames.size() > 0 && "clean".equals(taskNames.get(0))) {
            ConfigFileMgr.getInstance().deleteConfig();
        }

        Log.e("INIT Plugin Attach");
        AppExtension appExtension = project.getExtensions().findByType(AppExtension.class);
        if (appExtension != null) {
            appExtension.registerTransform(new InitTransform(project));
        } else {
            Log.e("appExtension is null");
        }
    }
}
