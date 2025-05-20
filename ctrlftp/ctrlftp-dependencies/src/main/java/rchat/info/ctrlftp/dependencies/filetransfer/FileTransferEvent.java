package rchat.info.ctrlftp.dependencies.filetransfer;

import java.io.File;

/**
 * An interface to call when file is transfered by
 * file accept transfer dependency
 */
public interface FileTransferEvent {
    void onTransferred();

    void onError(Exception e);
}
