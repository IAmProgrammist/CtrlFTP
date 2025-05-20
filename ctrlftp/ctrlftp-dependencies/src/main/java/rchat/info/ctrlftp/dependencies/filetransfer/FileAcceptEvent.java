package rchat.info.ctrlftp.dependencies.filetransfer;

import java.io.File;

/**
 * An interface to call when file is accepted by
 * file accept transfer dependency
 */
public interface FileAcceptEvent {
    void onAccept(File tempFile);

    void onError(Exception e);
}
