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
    private int clientPort = 20;
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
     * Updates client port. Warning: stops current data connection if new client port is different
     *
     * @param clientPort a new client port value
     */
    public void setClientPort(int clientPort) throws IOException {
        if (this.clientPort != clientPort)
            disconnect();

        this.clientPort = clientPort;
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
                this.server = new ServerSocket();
            } else {
                this.server = null;
            }

            this.isPassive = isPassive;
        }
    }

    /**
     * Gets server info if it runs in passive mode
     */
    public AbstractMap.SimpleEntry<String, Integer> getServerInfo() throws UnknownHostException {
        if (this.isPassive && this.server != null && !this.server.isClosed()) {
            return new AbstractMap.SimpleEntry<>(InetAddress.getLocalHost().toString(), this.server.getLocalPort());
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
        if (clientSocket != null && clientSocket.isConnected()) {
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
        if (clientSocket.isConnected()) return;

        if (this.isPassive) {
            if (this.server != null) {
                clientSocket = this.server.accept();
            }

            throw new RuntimeException("Server is not initialized");
        } else {
            clientSocket = new Socket(
                    this.session.getRemoteSocketAddress().toString(),
                    this.clientPort);
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
                throw new AcceptTransferIsBusy("File accept transfer is busy right now");
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
                throw new AcceptTransferIsBusy("File accept transfer is busy right now");
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
