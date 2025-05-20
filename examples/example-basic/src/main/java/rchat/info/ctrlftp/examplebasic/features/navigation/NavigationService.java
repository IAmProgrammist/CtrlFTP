package rchat.info.ctrlftp.examplebasic.features.navigation;

import rchat.info.ctrlftp.core.annotations.Command;
import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;
import rchat.info.ctrlftp.dependencies.deserializer.SingleStringDeserializer;
import rchat.info.ctrlftp.examplebasic.features.authentication.BasicAuthenticationDependency;

public class NavigationService {
    @Command(name = "PWD")
    public static Response PWD(BasicAuthenticationDependency auth,
                               NavigationDependency navigation) {
        var authResult = auth.authenticate();
        if (!authResult.isAuthenticated())
            return authResult.cause();

        return new Response(ResponseTypes.PATHNAME_CREATED, String.format("\"%s\" is current directory.",
                navigation.getCurrentFolder().toString()
                        .replace("\\", "/")));
    }

    @Command(name = "CWD")
    public static Response CWD(BasicAuthenticationDependency auth,
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
}
