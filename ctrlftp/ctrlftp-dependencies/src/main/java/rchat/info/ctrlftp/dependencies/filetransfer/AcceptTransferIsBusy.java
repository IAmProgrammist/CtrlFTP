package rchat.info.ctrlftp.dependencies.filetransfer;

public class AcceptTransferIsBusy extends RuntimeException {
    public AcceptTransferIsBusy(String message) {
        super(message);
    }
}
