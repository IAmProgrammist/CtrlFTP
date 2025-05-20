package rchat.info.ctrlftp.examplebasic.features.authentication;

import rchat.info.ctrlftp.core.Session;
import rchat.info.ctrlftp.core.annotations.Command;
import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;
import rchat.info.ctrlftp.dependencies.deserializer.SingleStringDeserializer;

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

    @Command(name = "ACCT")
    public static Response onAcct(BasicAuthenticationDependency auth) {
        var authResponse = auth.authenticate();

        if (authResponse.isAuthenticated()) {
            return new Response(ResponseTypes.COMMAND_OK,
                    String.format("You are logged in as a %s", authResponse.authInfo().getLogin()));
        } else return authResponse.cause();
    }

    @Command(name = "REIN")
    public static Response onRein(BasicAuthenticationDependency auth) {
        // TODO: add a dependency to stop any file transfers
        auth.logout();

        return new Response(ResponseTypes.COMMAND_OK, "Logout succesfully");
    }

    @Command(name = "QUIT")
    public static Response onQuit(BasicAuthenticationDependency auth, Session systemSessionContext) {
        // TODO: add a dependency to stop any file transfers
        auth.logout();
        systemSessionContext.disconnect(new Response(ResponseTypes.COMMAND_OK, "Quitted"));

        return null;
    }
}
