package rchat.info.ctrlftp.examplebasic.features.service;

import rchat.info.ctrlftp.core.annotations.Command;
import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;

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
}
