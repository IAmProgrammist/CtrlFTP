package rchat.info.ctrlftp.dependencies.filetransfer;

import rchat.info.ctrlftp.core.Session;
import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;

import java.io.IOException;
import java.net.Socket;

@Dependency(level = DependencyLevel.SESSION)
public class FileTransferDependency extends AbstractDependency {
    private int clientPort = 20;
    private Socket clientSocket = null;
    private Session session;

    public FileTransferDependency(Session session) {
        this.session = session;
    }

    /**
     * Updates client port. Warning: stops current data connection if new client port is different
     * @param clientPort a new client port value
     */
    public void setClientPort(int clientPort) throws IOException {
        if (this.clientPort != clientPort)
            disconnect();

        this.clientPort = clientPort;
    }

    /**
     * Closes current connection
     * @throws IOException
     */
    public void disconnect() throws IOException {
        if (clientSocket != null && clientSocket.isConnected()) {
            clientSocket.close();
        }
    }

    /**
     * Connects to a client
     * @throws IOException
     */
    private void connect() throws IOException {
        if (clientSocket.isConnected()) return;

        clientSocket =
    }
}
