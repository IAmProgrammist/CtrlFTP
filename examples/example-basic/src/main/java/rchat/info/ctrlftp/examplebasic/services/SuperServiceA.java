package rchat.info.ctrlftp.examplebasic.services;

import rchat.info.ctrlftp.core.annotations.Command;
import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;
import rchat.info.ctrlftp.examplebasic.dependencies.GlobalDependencyA;
import rchat.info.ctrlftp.examplebasic.dependencies.SuperDependencyC;

public class SuperServiceA {
    @Command(name = "USER")
    public static Response getHello(String command, SuperDependencyC depC, GlobalDependencyA ga) {
        return new Response(ResponseTypes.USERNAME_OK_NEED_PASS, "Need a password");
    }
}
