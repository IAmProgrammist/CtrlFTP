package rchat.info.ctrlftp.examplebasic.services.authentication;

import rchat.info.ctrlftp.core.annotations.Command;
import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;
import rchat.info.ctrlftp.dependencies.deserializer.SingleStringDeserializer;
import rchat.info.ctrlftp.examplebasic.dependencies.authentication.BasicAuthentication;

public class BasicAuthenticationService {
    @Command(name = "USER")
    public static Response onUser(BasicAuthentication auth, SingleStringDeserializer arg) {
        auth.setLogin(arg.getDeserializeData().arg());

        var authResponse = auth.authenticate();
        return authResponse.cause();
    }

    @Command(name = "PASS")
    public static Response onPassword(BasicAuthentication auth, SingleStringDeserializer arg) {
        auth.setPassword(arg.getDeserializeData().arg());

        var authResponse = auth.authenticate();
        return authResponse.cause();
    }

    @Command(name = "ACCT")
    public static Response onAcct(BasicAuthentication auth) {
        var authResponse = auth.authenticate();

        if (authResponse.isAuthenticated()) {
            return new Response(ResponseTypes.COMMAND_OK,
                    String.format("You are logged in as a %s", authResponse.authInfo().getLogin()));
        } else return authResponse.cause();
    }
}
