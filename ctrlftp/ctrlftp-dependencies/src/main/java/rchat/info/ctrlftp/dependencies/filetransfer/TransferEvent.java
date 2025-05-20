package rchat.info.ctrlftp.dependencies.filetransfer;

/**
 * An interface to call when file is transfered by
 * file accept transfer dependency
 */
public interface TransferEvent {
    /**
     * A callback
     */
    void onTransferred();

    void onError(Exception e);
}
