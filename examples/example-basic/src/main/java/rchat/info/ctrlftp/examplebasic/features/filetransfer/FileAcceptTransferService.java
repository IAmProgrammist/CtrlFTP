package rchat.info.ctrlftp.examplebasic.features.filetransfer;

import rchat.info.ctrlftp.core.annotations.Command;
import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;
import rchat.info.ctrlftp.examplebasic.features.authentication.BasicAuthenticationDependency;
import rchat.info.ctrlftp.examplebasic.features.authentication.BasicAuthenticationService;

import java.io.IOException;
import java.util.AbstractMap;

public class FileAcceptTransferService {
    @Command(name = "PORT")
    public static Response dataPort(
            DataPortDeserializer dataPort,
            FileAcceptTransferDependency fileAcceptTransferDependency,
            BasicAuthenticationDependency auth) {
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
                                      BasicAuthenticationDependency auth) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
                return authResult.cause();

        try {
            fileAcceptTransferDependency.setPassive(true);
            var serverInfo = fileAcceptTransferDependency.getServerInfo();

            return new Response(ResponseTypes.ENTERING_PASSIVE_MODE, serverInfo.getKey() + ":" + serverInfo.getValue());
        } catch (IOException e) {
            return new Response(ResponseTypes.SERVICE_NOT_AVAILABLE, "Couldn't make current server passive");
        }
    }

    @Command(name = "EPSV")
    public static Response setEPassive(FileAcceptTransferDependency fileAcceptTransferDependency,
                                       BasicAuthenticationDependency auth) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        try {
            fileAcceptTransferDependency.setPassive(true);
            var serverInfo = fileAcceptTransferDependency.getServerInfo();

            return new Response(ResponseTypes.ENTERING_PASSIVE_MODE, String.format("Entering Extended Passive Mode (|||%d|)", serverInfo.getValue()));
        } catch (IOException e) {
            return new Response(ResponseTypes.SERVICE_NOT_AVAILABLE, "Couldn't make current server passive");
        }
    }

    @Command(name = "TYPE")
    public static Response setType(BasicAuthenticationDependency auth) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        return new Response(ResponseTypes.COMMAND_OK);
    }
}
