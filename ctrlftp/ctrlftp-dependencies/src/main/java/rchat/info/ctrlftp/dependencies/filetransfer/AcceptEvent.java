package rchat.info.ctrlftp.dependencies.filetransfer;

/**
 * An interface to call when file is accepted by
 * file accept transfer dependency
 *
 * @param <DataClass> a program specific data class to store incoming data
 */
public interface AcceptEvent<DataClass> {
    void onAccept(DataClass tempFile);

    void onError(Exception e);
}
