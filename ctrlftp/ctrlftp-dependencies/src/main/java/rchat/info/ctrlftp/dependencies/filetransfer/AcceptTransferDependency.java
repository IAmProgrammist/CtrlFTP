package rchat.info.ctrlftp.dependencies.filetransfer;

import rchat.info.ctrlftp.core.Session;
import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.AbstractMap;

/**
 * Dependency to handle file transfering and accepting
 *
 * @param <Pipe> a piping class that transforms user input to readable data classes and backwards
 * @param <DataClass> a data class that stores user input and output
 */
@Dependency(level = DependencyLevel.SESSION)
public class AcceptTransferDependency<Pipe extends BasePipeDependency<DataClass>, DataClass> extends AbstractDependency {
    private final Session session;
    private AbstractMap.SimpleEntry<String, Integer> clientAddress = new AbstractMap.SimpleEntry<>("", 20);
    private Socket clientSocket = null;
    private ServerSocket server = null;
    private Thread acceptTransferThread = null;
    private final Pipe pipeClass;
    private boolean isPassive = false;

    public AcceptTransferDependency(Session session, Pipe pipeClass) {
        this.session = session;
        this.pipeClass = pipeClass;
    }

    /**
     * Updates client address and port. Warning: stops current data connection if new client port is different
     *
     * @param address a new client address and port
     */
    public void setClientAddress(AbstractMap.SimpleEntry<String, Integer> address) throws IOException {
        if (!this.clientAddress.equals(address))
            disconnect();

        this.clientAddress = address;
    }

    /**
     * Sets mode passive or active. If current mode and passed mode are not equal, throws an error
     *
     * @param isPassive sets
     */
    public void setPassive(boolean isPassive) throws IOException {
        if (this.isPassive != isPassive) {
            try {
                disconnect();
            } catch (IOException _) {
            }

            if (isPassive) {
                this.server = new ServerSocket(0);
            } else {
                this.server = null;
            }

            this.isPassive = isPassive;
        }
    }

    public boolean isPassive() {
        return isPassive;
    }

    /**
     * Gets server info if it runs in passive mode
     */
    public AbstractMap.SimpleEntry<String, Integer> getServerInfo() throws UnknownHostException {
        if (this.isPassive && this.server != null && !this.server.isClosed()) {
            var remoteSocketAddress = session.getRemoteSocketAddress().toString();
            return new AbstractMap.SimpleEntry<>(remoteSocketAddress.substring(1, remoteSocketAddress.indexOf(":")),
                    this.server.getLocalPort());
        }

        return null;
    }

    /**
     * Closes current connection and interrupts sending/accepting thread
     *
     * @throws IOException
     */
    public void disconnect() throws IOException {
        if (this.acceptTransferThread != null && this.acceptTransferThread.isAlive()) {
            this.acceptTransferThread.interrupt();
        }
        if (clientSocket != null && !clientSocket.isClosed()) {
            clientSocket.close();
        }
    }

    /**
     * Connects this.clientSocket to a client data port in active mode and awaits for client
     * connection in passive mode
     *
     * @throws IOException
     */
    private void connect() throws IOException {
        if (clientSocket != null && !clientSocket.isClosed()) return;

        if (this.isPassive) {
            if (this.server == null) {
                this.server = new ServerSocket(0);
            }

            clientSocket = this.server.accept();
        } else {
            if (this.server != null && !this.server.isClosed()) {
                this.server.close();
                this.server = null;
            }

            clientSocket = new Socket(
                    this.clientAddress.getKey() == null ?
                            this.session.getRemoteSocketAddress().toString() :
                            this.clientAddress.getKey(),
                    this.clientAddress.getValue());
        }
    }

    /**
     * Makes server to accept a file asyncrhonously.
     *
     * @param events calls onAccept method if file accepted
     *               succesfully, or else onError
     */
    public void accept(AcceptEvent<DataClass> events) {
        try {
            if (this.acceptTransferThread != null && this.acceptTransferThread.isAlive()) {
                throw new AcceptTransferIsBusy("Accept transfer is busy right now");
            }

            this.acceptTransferThread = Thread.ofVirtual().start(() -> {
                InputStream clientInput = null;
                try {
                    connect();

                    clientInput = this.clientSocket.getInputStream();
                    var parsedData = this.pipeClass.pipeClientInputToDataClass(clientInput);
                    events.onAccept(parsedData);
                } catch (Exception e) {
                    events.onError(e);
                } finally {
                    if (clientInput != null) {
                        try {
                            clientInput.close();
                        } catch (IOException _) {

                        }
                    }
                }

                try {
                    disconnect();
                } catch (IOException _) {
                }
            });
        } catch (Exception e) {
            events.onError(e);
        }
    }

    /**
     * Sends a data to a client
     *
     * @param data a path to a data
     * @param events a callback to call onError and onTransferred events
     */
    public void send(DataClass data, TransferEvent events) {
        try {
            if (this.acceptTransferThread != null && this.acceptTransferThread.isAlive()) {
                throw new AcceptTransferIsBusy("Accept transfer is busy right now");
            }

            this.acceptTransferThread = Thread.ofVirtual().start(() -> {
                OutputStream clientOutput = null;
                try {
                    connect();

                    clientOutput = this.clientSocket.getOutputStream();
                    this.pipeClass.pipeDataClassToClient(data, clientOutput);
                    events.onTransferred();
                } catch (Exception e) {
                    events.onError(e);
                } finally {
                    if (clientOutput != null) {
                        try {
                            clientOutput.close();
                        } catch (IOException _) {
                        }
                    }
                }

                try {
                    disconnect();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            events.onError(e);
        }
    }
}
