package rchat.info.ctrlftp.examplebasic.features.service;

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
        if (!file.toFile().isFile())
            return new Response(ResponseTypes.BAD_PARAMETERS, "Invalid filename");

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
        if (!file.toFile().isFile())
            return new Response(ResponseTypes.BAD_PARAMETERS, "Invalid filename");

        fileTransfer.accept(new AcceptEvent<FilePipeRecord>() {
            private File getUniqueFileName(String folderName, String searchedFilename, String path) {
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

            private File getUniqueFileName(String path) {
                int num = 1;
                String extension = getExtension(path);
                String filenameWithoutExtension = path.substring(0, path.lastIndexOf("."));
                File file = new File(filename);
                while (file.exists()) {
                    searchedFilename = filename + "(" + (num++) + ")" + extension;
                    file = new File(folderName, searchedFilename);
                }
                return file;
            }

            @Override
            public void onAccept(FilePipeRecord tempFile) {
                var oldFile = file.toFile();
                if (!oldFile.exists()) {
                    tempFile.iFile().renameTo(new File(oldFile.getAbsolutePath()));
                } else {
                    tempFile.iFile().renameTo(new File(getUniqueFileName(
                            oldFile.getParent(),

                    )));
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
}
