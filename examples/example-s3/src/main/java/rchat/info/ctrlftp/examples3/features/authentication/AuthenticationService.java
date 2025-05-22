package rchat.info.ctrlftp.examples3.features.authentication;

import rchat.info.ctrlftp.core.annotations.Command;
import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.dependencies.deserializer.SingleStringDeserializer;

public class AuthenticationService {
    @Command(name = "USER")
    public static Response setLogin(AuthenticationDependency auth,
                                    SingleStringDeserializer arg) {
        auth.setLogin(arg.getDeserializeData().arg());
        var authData = auth.authenticate();

        return authData.cause();
    }

    @Command(name = "PASS")
    public static Response setPass(AuthenticationDependency auth,
                                    SingleStringDeserializer arg) {
        auth.setPassword(arg.getDeserializeData().arg());
        var authData = auth.authenticate();

        return authData.cause();
    }
}
