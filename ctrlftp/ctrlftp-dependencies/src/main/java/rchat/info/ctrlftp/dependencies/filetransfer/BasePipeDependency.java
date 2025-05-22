package rchat.info.ctrlftp.dependencies.filetransfer;

import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;

import java.io.InputStream;
import java.io.OutputStream;
/**
 * The purpose of this class is transform client data of type A
 * and transform it into system-specific data type and backwards
 */
@Dependency(level = DependencyLevel.SESSION)
public abstract class BasePipeDependency<DataClass> extends AbstractDependency {
    /**
     * Pipes client specific input to a implementation specific data class
     * @param client a client raw input
     * @return parsed DataClass
     */
    public abstract DataClass pipeClientInputToDataClass(InputStream client);

    /**
     * Pipes specific data structure to a raw user content
     * @param data a specific data structure
     * @param client a client raw output
     */
    public abstract void pipeDataClassToClient(DataClass data, OutputStream client);
}
