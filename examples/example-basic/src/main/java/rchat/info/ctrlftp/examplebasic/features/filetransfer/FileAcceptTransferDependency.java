package rchat.info.ctrlftp.examplebasic.features.filetransfer;

import rchat.info.ctrlftp.core.Session;
import rchat.info.ctrlftp.dependencies.filetransfer.AcceptTransferDependency;

import java.io.File;

public class FileAcceptTransferDependency extends AcceptTransferDependency<FilePipeDependency, FilePipeRecord> {
    public FileAcceptTransferDependency(Session session, FilePipeDependency pipeClass) {
        super(session, pipeClass);
    }
}
