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
     * Connects this.clientSocket to a client data port
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
                    pipeReaderToWriter(
                            new InputStreamReader(this.clientSocket.getInputStream()),
                            new OutputStreamWriter(new FileOutputStream(temporaryFile))
                    );
                    events.onAccept(temporaryFile);
                } catch (Exception e) {
                    events.onError(e);
                } finally {
                    if (temporaryFile != null)
                        temporaryFile.delete();
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

    /**
     * Sends a file to a client
     *
     * @param file a path to a file
     * @param events a callback to call onError and onTransferred events
     */
    public void send(File file, FileTransferEvent events) {
        try {
            if (this.acceptTransferThread != null && this.acceptTransferThread.isAlive()) {
                throw new FileAcceptTransferIsBusy("File accept transfer is busy right now");
            }

            connect();

            this.acceptTransferThread = Thread.ofVirtual().start(() -> {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    pipeReaderToWriter(
                            new InputStreamReader(fis),
                            new OutputStreamWriter(this.clientSocket.getOutputStream())
                    );
                    events.onTransferred();
                } catch (Exception e) {
                    events.onError(e);
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            events.onError(e);
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

    /**
     * Pipes data from a reader to a writer
     * @param reader a reader
     * @param writer a writer
     */
    protected static void pipeReaderToWriter(Reader reader, Writer writer) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        BufferedReader bufferedReader = new BufferedReader(reader);
        char[] buffer = new char[8192];
        int readAmount;
        while ((readAmount = bufferedReader.read(buffer)) != -1) {
            bufferedWriter.write(buffer, 0, readAmount);
        }

        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
