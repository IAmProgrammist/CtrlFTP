package rchat.info.ctrlftp.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A
 */
public class Server {
    private ServerSocket serverSocket;
    private ExecutorService sessions;
    private Set<Class<?>> services;
    private Set<Class<?>> dependencies;

    public Server() {
        sessions = Executors.newVirtualThreadPerTaskExecutor();
        loadServices();
    }

    public void mainLoop() throws IOException {
        serverSocket = new ServerSocket();
        while (!serverSocket.isClosed()) {
            Socket client = serverSocket.accept();
            sessions.submit(new Session(this, client));
        }
    }

    public void shutdown() {
        sessions.shutdown();
    }

    /**
     * Loads service classes from services.xml file
     */
    public void loadServices() {
    }

    /**
     * Loads dependencies from dependencies.xml file
     */
    private void loadDependencies() {
    }

    public Set<Class<?>> getServices() {
        return services;
    }

    public Set<Class<?>> getDependencies() {
        return dependencies;
    }
}
