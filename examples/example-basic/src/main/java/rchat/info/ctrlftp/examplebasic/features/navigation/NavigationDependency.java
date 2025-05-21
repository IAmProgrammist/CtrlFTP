package rchat.info.ctrlftp.examplebasic.features.navigation;

import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;
import rchat.info.ctrlftp.examplebasic.features.service.OSValidator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
        if (Paths.get(path).isAbsolute()) {
            currentFolder = Paths.get(path).toAbsolutePath();
        } else {
            currentFolder = Paths.get(currentFolder.toAbsolutePath() + "/" + path).toAbsolutePath();
        }
    }

    public List<String> getFilesNames(Path path) throws IOException {
        List<String> result = new ArrayList<>();
//        try (Stream<Path> paths = Files.walk(path, 1)) {
//            result = paths
//                    .filter(Files::isRegularFile)
//                    .map(Path::getFileName)
//                    .map(Path::toString)
//                    .toList();
//        }
//
//        return result;

        if (OSValidator.isWindows()) {
            // TODO: implement LS for windows
        } else {
            try {
                Process process = new ProcessBuilder()
                        .command("bash", "-c",
                                "export LC_ALL=en_US.UTF-8 ; " +
                                        "ls -la")
                        .directory(path.toFile())
                        .start();

                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(process.getInputStream()));

                String s;
                while ((s = stdInput.readLine()) != null) {
                    result.add(s);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public List<String> getFilesNames() throws IOException {
        return getFilesNames(this.currentFolder);
    }
}