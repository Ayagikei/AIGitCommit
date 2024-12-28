package com.hmydk.aigit.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

public class ProjectUtil {

    public static Project getCurrentProject() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects.length == 0) {
            throw new IllegalStateException("No open projects found");
        }
        return openProjects[0];
    }

    public static String getCurrentProjectName() {
        return getCurrentProject().getName();
    }

    public static String getCurrentProjectId() {
        return getCurrentProject().getBasePath();
    }
}
