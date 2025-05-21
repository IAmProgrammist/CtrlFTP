package rchat.info.ctrlftp.dependencies.deserializer;

import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;

/**
 * A dependency which deserializes and parses command arguments
 * @param <DeserializeClass> a result deserialize class
 */
@Dependency(level = DependencyLevel.COMMAND)
public abstract class BaseDeserializer<DeserializeClass> extends AbstractDependency {
    private final DeserializeClass deserializeData;

    public BaseDeserializer(String command) {
        this.deserializeData = deserialize(command);
    }

    /**
     * A deserialize method that parses command and returns DeserializeClass
     * @param command a raw command
     * @return deserialized data
     */
    protected abstract DeserializeClass deserialize(String command);

    /**
     * A method that returns args from command
     * @return deserialized args from command
     */
    public DeserializeClass getDeserializeData() {
        return deserializeData;
    }
}
