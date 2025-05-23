package rchat.info.ctrlftp.examplebasic.features.service;

import rchat.info.ctrlftp.core.Server;
import rchat.info.ctrlftp.core.Session;
import rchat.info.ctrlftp.core.annotations.Command;
import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;
import rchat.info.ctrlftp.dependencies.deserializer.SingleStringDeserializer;
import rchat.info.ctrlftp.dependencies.filetransfer.AcceptEvent;
import rchat.info.ctrlftp.dependencies.filetransfer.TransferEvent;
import rchat.info.ctrlftp.examplebasic.features.authentication.BasicAuthenticationDependency;
import rchat.info.ctrlftp.examplebasic.features.filetransfer.FileAcceptTransferDependency;
import rchat.info.ctrlftp.examplebasic.features.filetransfer.FilePipeRecord;
import rchat.info.ctrlftp.examplebasic.features.navigation.NavigationDependency;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ServiceService {
    @Command(name = "SYST")
    public static Response getSystemType(ServiceDependency serviceDependency) {
        var osName = serviceDependency.getSystemName();
        if (osName != null) {
            return new Response(ResponseTypes.SYSTEM_TYPE, osName);
        } else {
            return new Response(ResponseTypes.NOT_IMPLEMENTED, "Unable to recognize current system");
        }
    }

    @Command(name = "NOOP")
    public static Response noop() {
        return new Response(ResponseTypes.COMMAND_OK, "Glad to work with ya, human 'fella!");
    }

    @Command(name = "RETR")
    public static Response getFile(
            BasicAuthenticationDependency auth,
            FileAcceptTransferDependency fileTransfer,
            NavigationDependency navigation,
            SingleStringDeserializer args,
            Session session
    ) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        var file = navigation.getFile(args.getDeserializeData().arg());
        if (!file.isFile())
            return new Response(ResponseTypes.FILE_BUSY, "File doesnt exists at path");

        fileTransfer.send(new FilePipeRecord(file, null), new TransferEvent() {
            @Override
            public void onTransferred() {
                try {
                    session.sendResponse(new Response(ResponseTypes.FILE_ACTION_OK, "File sent successfully"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Exception e) {
                try {
                    session.sendResponse(new Response(ResponseTypes.FILE_BUSY));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        return new Response(ResponseTypes.ABOUT_TO_OPEN_CONNECTION, "Ready to send data");
    }

    @Command(name = "STOR")
    public static Response storeFile(
            BasicAuthenticationDependency auth,
            FileAcceptTransferDependency fileTransfer,
            NavigationDependency navigation,
            SingleStringDeserializer args,
            Session session
    ) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        var file = navigation.getPathRelativeToCWD(args.getDeserializeData().arg());

        fileTransfer.accept(new AcceptEvent<FilePipeRecord>() {
            @Override
            public void onAccept(FilePipeRecord tempFile) {
                var oldFile = file.toFile();
                oldFile.delete();
                tempFile.iFile().renameTo(new File(oldFile.getAbsolutePath()));

                try {
                    session.sendResponse(new Response(ResponseTypes.FILE_ACTION_OK, "File moved successfully"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Exception e) {
                try {
                    session.sendResponse(new Response(ResponseTypes.FILE_BUSY));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        return new Response(ResponseTypes.ABOUT_TO_OPEN_CONNECTION, "Ready to send data");
    }

    @Command(name = "STOU")
    public static Response storeUniqueFile(
            BasicAuthenticationDependency auth,
            FileAcceptTransferDependency fileTransfer,
            NavigationDependency navigation,
            SingleStringDeserializer args,
            Session session
    ) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        var file = navigation.getPathRelativeToCWD(args.getDeserializeData().arg());

        fileTransfer.accept(new AcceptEvent<FilePipeRecord>() {
            private File getUniqueFile(String folderName, String searchedFilename) {
                int num = 1;
                String extension = getExtension(searchedFilename);
                String filename = searchedFilename.substring(0, searchedFilename.lastIndexOf("."));
                File file = new File(folderName, searchedFilename);
                while (file.exists()) {
                    searchedFilename = filename + "(" + (num++) + ")" + extension;
                    file = new File(folderName, searchedFilename);
                }
                return file;
            }

            private String getExtension(String name) {
                return name.substring(name.lastIndexOf("."));
            }

            @Override
            public void onAccept(FilePipeRecord tempFile) {
                var oldFile = file.toFile();
                var uniqueFile = getUniqueFile(
                        oldFile.getParent(),
                        oldFile.getName()
                );
                tempFile.iFile().renameTo(uniqueFile);

                try {
                    session.sendResponse(new Response(ResponseTypes.FILE_ACTION_OK, String.format("\"%s\" saved with this name",
                            uniqueFile.getName())));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Exception e) {
                try {
                    session.sendResponse(new Response(ResponseTypes.FILE_BUSY));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        return new Response(ResponseTypes.ABOUT_TO_OPEN_CONNECTION, "Ready to send data");
    }

    @Command(name = "APPE")
    public static Response appendFile(
            BasicAuthenticationDependency auth,
            FileAcceptTransferDependency fileTransfer,
            NavigationDependency navigation,
            SingleStringDeserializer args,
            Session session
    ) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        var file = navigation.getPathRelativeToCWD(args.getDeserializeData().arg());
        if (!file.toFile().isFile())
            return new Response(ResponseTypes.BAD_PARAMETERS, "Invalid filename");

        fileTransfer.accept(new AcceptEvent<FilePipeRecord>() {
            @Override
            public void onAccept(FilePipeRecord tempFile) {
                var oldFile = file.toFile();
                if (oldFile.exists()) {
                    tempFile.iFile().renameTo(new File(oldFile.getAbsolutePath()));
                } else {
                    try {
                        Files.write(
                                file,
                                Files.readAllBytes(Paths.get(tempFile.iFile().getAbsoluteFile().toString())),
                                StandardOpenOption.APPEND
                        );
                    } catch (IOException e) {
                        try {
                            session.sendResponse(new Response(ResponseTypes.FILENAME_NOT_ALLOWED, "Couldn't append a file"));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }

                try {
                    session.sendResponse(new Response(ResponseTypes.FILE_ACTION_OK, "File moved successfully"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Exception e) {
                try {
                    session.sendResponse(new Response(ResponseTypes.FILE_BUSY));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        return new Response(ResponseTypes.ABOUT_TO_OPEN_CONNECTION, "Ready to send data");
    }

    @Command(name = "RNFR")
    public static Response renameFrom(
            BasicAuthenticationDependency auth,
            NavigationDependency navigation,
            SingleStringDeserializer args
    ) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        navigation.setRenameFrom(args.getDeserializeData().arg());

        return new Response(ResponseTypes.COMMAND_OK, "Ready to send data");
    }

    @Command(name = "RNTO")
    public static Response renameTo(
            BasicAuthenticationDependency auth,
            NavigationDependency navigation,
            SingleStringDeserializer args
    ) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        return navigation.renameTo(args.getDeserializeData().arg());
    }

    @Command(name = "ABOR")
    public static Response abort(
            BasicAuthenticationDependency auth,
            FileAcceptTransferDependency transfer) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        try {
            transfer.disconnect();
        } catch (IOException e) {
            return new Response(ResponseTypes.REQUESTED_ACTION_NOT_TAKEN, "Unable to abort file transfer");
        }

        return new Response(ResponseTypes.CLOSING_DATA_CONNECTION, "Closing data connection");
    }

    @Command(name = "DELE")
    public static Response delete(
            BasicAuthenticationDependency auth,
            NavigationDependency navi,
            SingleStringDeserializer arg) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        var file = navi.getFile(arg.getDeserializeData().arg());
        if (!file.exists() || !file.isFile() || !file.delete()) {
            return new Response(ResponseTypes.FILE_BUSY, "File doesn't exists");
        }

        return new Response(ResponseTypes.FILE_ACTION_OK, "File deleted successfully");
    }

    static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    @Command(name = "RMD")
    public static Response removeDirectory(
            BasicAuthenticationDependency auth,
            NavigationDependency navi,
            SingleStringDeserializer arg) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        var file = navi.getFile(arg.getDeserializeData().arg());
        if (!file.exists() || !file.isDirectory()) {
            return new Response(ResponseTypes.FILE_BUSY, "Folder doesn't exists");
        }

        deleteDir(file);

        return new Response(ResponseTypes.FILE_ACTION_OK, "Directory deleted successfully");
    }

    @Command(name = "MKD")
    public static Response makeDirectory(
            BasicAuthenticationDependency auth,
            NavigationDependency navi,
            SingleStringDeserializer arg) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        var file = navi.getFile(arg.getDeserializeData().arg());
        if (!file.mkdirs()) {
            return new Response(ResponseTypes.FILE_BUSY, "Unable to create directory");
        }

        return new Response(ResponseTypes.FILE_ACTION_OK, "Directory created succesfully");
    }
}
