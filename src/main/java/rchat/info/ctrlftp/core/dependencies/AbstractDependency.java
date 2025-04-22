package rchat.info.ctrlftp.core.dependencies;

public abstract class AbstractDependency {
    private final DependencyLevel level = null;
    private final String command = null;

    AbstractDependency() {
    }

    /**
     * Gives access to a level of a dependency
     * @return current dependency level
     */
    public DependencyLevel getLevel() {
        return level;
    }

    /**
     * Gives access to a command. Only for command-leveled dependencies
     * @return a raw command with parameters
     */
    public String getCommand() {
        return command;
    }
}
