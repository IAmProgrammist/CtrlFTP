package rchat.info.ctrlftp.dependencies.deserializer;

/**
 * A dependency to parse single string after a command name
 */
public class SingleStringDeserializer extends BaseDeserializer<SingleStringDeserialized> {
    public SingleStringDeserializer(String command) {
        super(command);
    }

    @Override
    public SingleStringDeserialized deserialize(String command) {
        var firstSpaceIndex = command.indexOf(' ');

        return new SingleStringDeserialized(firstSpaceIndex == -1 ? "" : command.substring(firstSpaceIndex + 1));
    }
}
