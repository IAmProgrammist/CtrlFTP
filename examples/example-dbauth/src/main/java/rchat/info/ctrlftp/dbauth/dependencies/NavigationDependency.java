package rchat.info.ctrlftp.dbauth.dependencies;

import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;
import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;
import rchat.info.ctrlftp.dbauth.utils.OSValidator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Dependency(level = DependencyLevel.SESSION)
public class NavigationDependency extends AbstractDependency {
    private Path currentFolder;
    private Path renameFrom;

    public NavigationDependency() {
        currentFolder = Paths.get("").toAbsolutePath();
    }

    public Path getCurrentFolder() {
        return currentFolder;
    }

    public Path getPathRelativeToCWD(String path) {
        Path resultPath;
        if (Paths.get(path).isAbsolute()) {
            resultPath = Paths.get(path).toAbsolutePath();
        } else {
            resultPath = Paths.get(currentFolder.toAbsolutePath() + "/" + path).toAbsolutePath();
        }

        return resultPath;
    }

    public void changeWorkingDirectory(String path) {
        this.currentFolder = getPathRelativeToCWD(path);
    }

    public File getFile(String path) {
        return getPathRelativeToCWD(path).toFile();
    }

    private List<String> getFilesNames(Path path, boolean shortened) throws IOException {
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
                                        (shortened ? "ls -1 -a" : "ls -la"))
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



    public List<String> getFilesNames(Path path) throws IOException {
        return getFilesNames(path, false);
    }

    public List<String> getFilesNamesShort(Path path) throws IOException {
        return getFilesNames(path, true);
    }

    public List<String> getFilesNames() throws IOException {
        return getFilesNames(this.currentFolder);
    }

    public List<String> getFilesNamesShort() throws IOException {
        return getFilesNamesShort(this.currentFolder);
    }

    public void setRenameFrom(String path) {
        this.renameFrom = getPathRelativeToCWD(path);
    }

    public Response renameTo(String newPath) {
        if (this.renameFrom == null) {
            return new Response(ResponseTypes.FILENAME_NOT_ALLOWED, "You should setup rename from first");
        }

        var renameToPath = getPathRelativeToCWD(newPath);

        var succeeded = this.renameFrom.toFile().renameTo(renameToPath.toFile());

        if (!succeeded) {
            return new Response(ResponseTypes.FILENAME_NOT_ALLOWED, "Unabled to rename");
        }

        return new Response(ResponseTypes.FILE_ACTION_OK, "File renamed succesully");
    }
}