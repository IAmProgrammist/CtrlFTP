package rchat.info.ctrlftp.core.annotations;

import java.lang.annotation.*;

/**
 * A method annotated with {@link Command} would be called when a command with a
 * corresponding {@link Command#name} is sent. The namecheck is case-insensitive.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Command {
    /**
     * A command name
     */
    public String name();
}
