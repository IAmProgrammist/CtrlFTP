package rchat.info.ctrlftp.core.reflections;

import rchat.info.ctrlftp.core.Server;
import rchat.info.ctrlftp.core.annotations.Command;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * An utility class to find methods by command name
 */
public class MethodResolver {
    /**
     * A method for finding corresponding method to process command
     *
     * @param serverContext a server context
     * @param name a name of a command
     * @return method to be called
     */
    public static Optional<Method> findMethod(Server serverContext, String name) {
        var services = serverContext.getServiceClasses();

        for (var serviceClass : services) {
            for (var serviceMethod : serviceClass.getMethods()) {
                Command c = serviceMethod.getAnnotation(Command.class);

                if (c != null && name.equalsIgnoreCase(c.name())) return Optional.of(serviceMethod);
            }
        }

        return Optional.empty();
    }
}
