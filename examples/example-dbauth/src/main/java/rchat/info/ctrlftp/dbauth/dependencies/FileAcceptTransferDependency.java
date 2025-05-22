package rchat.info.ctrlftp.dbauth.dependencies;

import rchat.info.ctrlftp.core.Session;
import rchat.info.ctrlftp.dependencies.filetransfer.AcceptTransferDependency;

public class FileAcceptTransferDependency extends AcceptTransferDependency<FilePipeDependency, FilePipeRecord> {
    public FileAcceptTransferDependency(Session session, FilePipeDependency pipeClass) {
        super(session, pipeClass);
    }
}
