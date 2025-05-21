package rchat.info.ctrlftp.examplebasic.features.authentication;

import rchat.info.ctrlftp.core.Session;
import rchat.info.ctrlftp.core.annotations.Command;
import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;
import rchat.info.ctrlftp.dependencies.deserializer.SingleStringDeserializer;
import rchat.info.ctrlftp.examplebasic.features.filetransfer.FileAcceptTransferDependency;

import java.io.IOException;

public class BasicAuthenticationService {
    @Command(name = "USER")
    public static Response onUser(BasicAuthenticationDependency auth, SingleStringDeserializer arg) {
        auth.setLogin(arg.getDeserializeData().arg());

        var authResponse = auth.authenticate();
        return authResponse.cause();
    }

    @Command(name = "PASS")
    public static Response onPassword(BasicAuthenticationDependency auth, SingleStringDeserializer arg) {
        auth.setPassword(arg.getDeserializeData().arg());

        var authResponse = auth.authenticate();
        return authResponse.cause();
    }

    @Command(name = "REIN")
    public static Response onRein(BasicAuthenticationDependency auth,
                                  FileAcceptTransferDependency fileAcceptTransferDependency) {
        try {
            fileAcceptTransferDependency.disconnect();
        } catch (IOException _) {
        }
        auth.logout();

        return new Response(ResponseTypes.COMMAND_OK, "Logout succesfully");
    }

    @Command(name = "QUIT")
    public static Response onQuit(BasicAuthenticationDependency auth, Session systemSessionContext,
                                  FileAcceptTransferDependency fileAcceptTransferDependency) {
        try {
            fileAcceptTransferDependency.disconnect();
        } catch (IOException _) {
        }
        auth.logout();
        systemSessionContext.disconnect(new Response(ResponseTypes.COMMAND_OK, "Quitted"));

        return null;
    }
}
