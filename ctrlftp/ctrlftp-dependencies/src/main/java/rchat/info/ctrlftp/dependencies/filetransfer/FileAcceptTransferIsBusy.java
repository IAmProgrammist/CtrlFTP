package rchat.info.ctrlftp.dependencies.filetransfer;

public class FileAcceptTransferIsBusy extends RuntimeException {
    public FileAcceptTransferIsBusy(String message) {
        super(message);
    }
}
