package rchat.info.ctrlftp.core.annotations;

import rchat.info.ctrlftp.core.DependencyLevel;

/**
 * A class annotated with {@link Dependency} will be able to be injected into
 * commands annotated by {@link Command} and dependency methods.
 * Level of dependency existence is passed using parameter {@link Dependency#level}
 */
public @interface Dependency {
    /**
     * Sets the level of dependency. Defaults to {@link DependencyLevel#SESSION}
     */
    public DependencyLevel level() default DependencyLevel.SESSION;
}
