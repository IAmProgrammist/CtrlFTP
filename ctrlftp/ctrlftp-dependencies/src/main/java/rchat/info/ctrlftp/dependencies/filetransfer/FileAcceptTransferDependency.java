package rchat.info.ctrlftp.dependencies.filetransfer;

import rchat.info.ctrlftp.core.Session;
import rchat.info.ctrlftp.core.annotations.Dependency;
import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyLevel;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Dependency to handle file transfering and accepting
 */
@Dependency(level = DependencyLevel.SESSION)
public class FileAcceptTransferDependency extends AbstractDependency {
    private final Session session;
    private int clientPort = 20;
    private Socket clientSocket = null;
    private Thread acceptTransferThread = null;

    public FileAcceptTransferDependency(Session session) {
        this.session = session;
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
     * Connects to a client
     *
     * @throws IOException
     */
    private void connect() throws IOException {
        if (clientSocket.isConnected()) return;

        clientSocket = new Socket(
                this.session.getRemoteSocketAddress().toString(),
                this.clientPort);
    }

    /**
     * Makes server to accept a file asyncrhonously.
     *
     * @param events calls onAccept method if file accepted
     *               succesfully, or else onError
     */
    public void accept(FileAcceptEvent events) {
        try {
            if (this.acceptTransferThread != null && this.acceptTransferThread.isAlive()) {
                throw new FileAcceptTransferIsBusy("File accept transfer is busy right now");
            }

            connect();

            this.acceptTransferThread = Thread.ofVirtual().start(() -> {
                File temporaryFile = null;
                try {
                    temporaryFile = File.createTempFile("accept-file-",
                            "file-accept-transfer-dependency");

                    BufferedWriter temporaryFileWriter = new BufferedWriter(
                            new PrintWriter(new FileOutputStream(temporaryFile))
                    );
                    BufferedReader r = new BufferedReader(
                            new InputStreamReader(this.clientSocket.getInputStream())
                    );
                    char[] buffer = new char[1024];
                    int readAmount;
                    while ((readAmount = r.read(buffer)) != -1) {
                        temporaryFileWriter.write(buffer, 0, readAmount);
                    }

                    temporaryFileWriter.flush();
                    temporaryFileWriter.close();
                    events.onAccept(temporaryFile);
                } catch (Exception e) {
                    events.onError(e);
                } finally {
                    if (temporaryFile != null)
                        temporaryFile.delete();
                }
            });
        } catch (Exception e) {
            events.onError(e);
        }
    }

    /**
     * Sends a file to a client
     */
    public void send(File file, FileTransferEvent events) {
        try {
            if (this.acceptTransferThread != null && this.acceptTransferThread.isAlive()) {
                throw new FileAcceptTransferIsBusy("File accept transfer is busy right now");
            }

            connect();

            this.acceptTransferThread = Thread.ofVirtual().start(() -> {
                try {
                    temporaryFile = File.createTempFile("accept-file-",
                            "file-accept-transfer-dependency");

                    BufferedWriter temporaryFileWriter = new BufferedWriter(
                            new PrintWriter(new FileOutputStream(temporaryFile))
                    );
                    BufferedReader r = new BufferedReader(
                            new InputStreamReader(this.clientSocket.getInputStream())
                    );
                    char[] buffer = new char[1024];
                    int readAmount;
                    while ((readAmount = r.read(buffer)) != -1) {
                        temporaryFileWriter.write(buffer, 0, readAmount);
                    }

                    temporaryFileWriter.flush();
                    temporaryFileWriter.close();
                    events.onAccept(temporaryFile);
                } catch (Exception e) {
                    events.onError(e);
                } finally {
                    if (temporaryFile != null)
                        temporaryFile.delete();
                }
            });
        } catch (Exception e) {
            events.onError(e);
        }
    }
}
