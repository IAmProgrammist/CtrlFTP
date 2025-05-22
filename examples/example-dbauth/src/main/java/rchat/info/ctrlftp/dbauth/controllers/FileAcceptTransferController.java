package rchat.info.ctrlftp.dbauth.controllers;

import rchat.info.ctrlftp.core.annotations.Command;
import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;
import rchat.info.ctrlftp.dbauth.dependencies.AuthenticationDependency;
import rchat.info.ctrlftp.dbauth.dependencies.deserializers.DataPortDeserializer;
import rchat.info.ctrlftp.dbauth.dependencies.FileAcceptTransferDependency;
import rchat.info.ctrlftp.dbauth.dependencies.FilePipeDependency;
import rchat.info.ctrlftp.dependencies.deserializer.SingleStringDeserializer;

import java.io.IOException;
import java.util.AbstractMap;

public class FileAcceptTransferController {
    @Command(name = "PORT")
    public static Response dataPort(
            DataPortDeserializer dataPort,
            FileAcceptTransferDependency fileAcceptTransferDependency,
            AuthenticationDependency auth) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        if (dataPort.getDeserializeData() == null)
            return new Response(ResponseTypes.BAD_PARAMETERS, "Invalid args");

        try {
            fileAcceptTransferDependency.setClientAddress(new AbstractMap.SimpleEntry<>(
                    dataPort.getDeserializeData().ip(),
                    dataPort.getDeserializeData().port()
            ));
        } catch (IOException e) {
            return new Response(ResponseTypes.SERVICE_NOT_AVAILABLE, "Couldn't close current connection");
        }

        return new Response(ResponseTypes.COMMAND_OK);
    }

    @Command(name = "PASV")
    public static Response setPassive(FileAcceptTransferDependency fileAcceptTransferDependency,
                                      AuthenticationDependency auth) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        try {
            fileAcceptTransferDependency.setPassive(true);
            var serverInfo = fileAcceptTransferDependency.getServerInfo();
            String[] ipBytes = serverInfo.getKey().split("\\.");

            return new Response(ResponseTypes.ENTERING_PASSIVE_MODE, String.format("Entering Passive Mode (%s,%s,%s,%s,%d,%d)",
                    ipBytes[0], ipBytes[1], ipBytes[2], ipBytes[3], serverInfo.getValue() >> 8,
                    serverInfo.getValue() & 0xFF));
        } catch (IOException e) {
            return new Response(ResponseTypes.SERVICE_NOT_AVAILABLE, "Couldn't make current server passive");
        }
    }

    @Command(name = "TYPE")
    public static Response setType(AuthenticationDependency auth,
                                   FilePipeDependency pipe,
                                   SingleStringDeserializer arg) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        return pipe.setType(arg.getDeserializeData().arg());
    }
}
