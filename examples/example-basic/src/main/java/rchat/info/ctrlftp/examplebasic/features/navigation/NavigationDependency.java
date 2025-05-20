package rchat.info.ctrlftp.examplebasic.features.navigation;

import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

@Dependency(level = DependencyLevel.SESSION)
public class NavigationDependency extends AbstractDependency {
    private Path currentFolder;

    public NavigationDependency() {
        currentFolder = Paths.get("").toAbsolutePath();
    }

    public Path getCurrentFolder() {
        return currentFolder;
    }

    public void changeWorkingDirectory(String path) {
        currentFolder = Paths.get(path).toAbsolutePath();
    }
}