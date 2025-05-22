package rchat.info.ctrlftp.dbauth.controllers;

import rchat.info.ctrlftp.core.Session;
import rchat.info.ctrlftp.core.annotations.Command;
import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;
import rchat.info.ctrlftp.dbauth.dependencies.FileAcceptTransferDependency;
import rchat.info.ctrlftp.dbauth.dependencies.FilePipeRecord;
import rchat.info.ctrlftp.dbauth.dependencies.NavigationDependency;
import rchat.info.ctrlftp.dependencies.deserializer.SingleStringDeserializer;
import rchat.info.ctrlftp.dependencies.filetransfer.TransferEvent;
import rchat.info.ctrlftp.dbauth.dependencies.AuthenticationDependency;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class NavigationController {
    @Command(name = "PWD")
    public static Response PWD(AuthenticationDependency auth,
                               NavigationDependency navigation) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        return new Response(ResponseTypes.PATHNAME_CREATED, String.format("\"%s\" is current directory.",
                navigation.getCurrentFolder().toString()
                        .replace("\\", "/")));
    }

    @Command(name = "CWD")
    public static Response CWD(AuthenticationDependency auth,
                               NavigationDependency navigation,
                               SingleStringDeserializer arg) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        try {
            navigation.changeWorkingDirectory(arg.getDeserializeData().arg());

            return new Response(ResponseTypes.PATHNAME_CREATED, String.format("\"%s\" is current directory.",
                    navigation.getCurrentFolder().toString()
                            .replace("\\", "/")));
        } catch (Exception e) {
            return new Response(ResponseTypes.BAD_PARAMETERS, "Bad filepath");
        }
    }

    @Command(name = "LIST")
    public static Response getList(AuthenticationDependency auth,
                                   FileAcceptTransferDependency transfer,
                                   NavigationDependency navigationDependency,
                                   SingleStringDeserializer args,
                                   Session session) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        try {
            List<String> filesList;
            if (args.getDeserializeData().arg().isEmpty()) {
                filesList = navigationDependency.getFilesNames();
            } else {
                filesList = navigationDependency.getFilesNames(Paths.get(args.getDeserializeData().arg()));
            }

            String data = String.join("\r\n", filesList);
            transfer.send(new FilePipeRecord(null, data), new TransferEvent() {
                @Override
                public void onTransferred() {
                    try {
                        session.sendResponse(
                                new Response(ResponseTypes.COMMAND_OK, "A file sent succesfully")
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    try {
                        session.sendResponse(
                                new Response(ResponseTypes.TRANSFER_ABORTED, "Failed to send a file")
                        );
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        } catch (IOException e) {
            return new Response(ResponseTypes.FILENAME_NOT_ALLOWED, "Couldn't get folder contents");
        }

        return new Response(ResponseTypes.ABOUT_TO_OPEN_CONNECTION, "Ready to send data");
    }

    @Command(name = "NLST")
    public static Response getListShort(AuthenticationDependency auth,
                                        FileAcceptTransferDependency transfer,
                                        NavigationDependency navigationDependency,
                                        SingleStringDeserializer args,
                                        Session session) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        try {
            List<String> filesList;
            if (args.getDeserializeData().arg().isEmpty()) {
                filesList = navigationDependency.getFilesNamesShort();
            } else {
                filesList = navigationDependency.getFilesNamesShort(Paths.get(args.getDeserializeData().arg()));
            }

            String data = String.join("\r\n", filesList);
            transfer.send(new FilePipeRecord(null, data), new TransferEvent() {
                @Override
                public void onTransferred() {
                    try {
                        session.sendResponse(
                                new Response(ResponseTypes.COMMAND_OK, "A file sent succesfully")
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    try {
                        session.sendResponse(
                                new Response(ResponseTypes.TRANSFER_ABORTED, "Failed to send a file")
                        );
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        } catch (IOException e) {
            return new Response(ResponseTypes.FILENAME_NOT_ALLOWED, "Couldn't get folder contents");
        }

        return new Response(ResponseTypes.ABOUT_TO_OPEN_CONNECTION, "Ready to send data");
    }
}
