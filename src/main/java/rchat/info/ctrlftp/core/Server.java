package rchat.info.ctrlftp.core;

import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyManager;

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
    private DependencyManager dependencyManager;
    private Set<Class<?>> serviceClasses;
    private Set<Class<? extends AbstractDependency>> dependencyClasses;

    public Server() {
        sessions = Executors.newVirtualThreadPerTaskExecutor();
        dependencyManager = new DependencyManager(this);
        loadServices();
        loadDependencies();
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

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public Set<Class<?>> getServiceClasses() {
        return serviceClasses;
    }

    public Set<Class<? extends AbstractDependency>> getDependencyClasses() {
        return dependencyClasses;
    }
}
