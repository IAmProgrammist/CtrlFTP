package rchat.info.ctrlftp.examplebasic.features.filetransfer;

import rchat.info.ctrlftp.dependencies.filetransfer.BasePipeDependency;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class FilePipeDependency extends BasePipeDependency<File> {
    @Override
    public File pipeClientInputToDataClass(InputStream client) {
        return null;
    }

    @Override
    public void pipeDataClassToClient(File data, OutputStream client) {

    }
}
