package rchat.info.ctrlftp.core.dependencies;

public enum DependencyLevel {
    /**
     * A dependency will be unique for server and will exist while server runs
     */
    GLOBAL,
    /**
     * A dependency will be unique for session and will exist while session exists
     */
    SESSION,
    /**
     * A dependency will be unique for command and will exist while command executes
     */
    COMMAND
}
