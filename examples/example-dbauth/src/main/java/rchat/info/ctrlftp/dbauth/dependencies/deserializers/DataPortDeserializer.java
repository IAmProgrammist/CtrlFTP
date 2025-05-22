package rchat.info.ctrlftp.dbauth.dependencies.deserializers;

import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;
import rchat.info.ctrlftp.dependencies.deserializer.BaseDeserializer;
import rchat.info.ctrlftp.dependencies.deserializer.SingleStringDeserializer;

/**
 * Deserializer for data port command
 */
@Dependency(level = DependencyLevel.COMMAND)
public class DataPortDeserializer extends BaseDeserializer<DataPortDeserializer.DataPort> {
    public DataPortDeserializer(SingleStringDeserializer deserializer) {
        super(deserializer.getDeserializeData().arg());
    }

    @Override
    public DataPort deserialize(String command) {
        String[] bytesArgs = command.split(",");
        if (bytesArgs.length == 6) {
            String ip = String.format("%s.%s.%s.%s",
                    bytesArgs[0], bytesArgs[1], bytesArgs[2], bytesArgs[3]);
            Integer port = Integer.parseInt(bytesArgs[4]) << 8 + Integer.parseInt(bytesArgs[5]);

            return new DataPort(ip, port);
        }
        return null;
    }

    public record DataPort(String ip, Integer port) {}
}
