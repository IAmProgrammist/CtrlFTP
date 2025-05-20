package rchat.info.ctrlftp.examplebasic.features.navigation;

import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<String> getFilesNames(Path path) throws IOException {
        List<String> result;
        try (Stream<Path> paths = Files.walk(path, 1)) {
            result = paths
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList();
        }

        return result;
    }

    public List<String> getFilesNames() throws IOException {
        return getFilesNames(this.currentFolder);
    }
}