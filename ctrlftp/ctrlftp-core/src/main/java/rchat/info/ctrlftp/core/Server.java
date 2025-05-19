package rchat.info.ctrlftp.core;

import rchat.info.ctrlftp.core.dependencies.AbstractDependency;
import rchat.info.ctrlftp.core.dependencies.DependencyManager;
import rchat.info.ctrlftp.core.reflections.ClassesLoader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * An FTP server
 */
public class Server {
    private ServerSocket serverSocket;
    private ExecutorService sessions;
    private DependencyManager dependencyManager;
    private Set<Class<?>> serviceClasses;
    private Set<Class<? extends AbstractDependency>> dependencyClasses;

    public Server(List<String> configs) {
        sessions = Executors.newVirtualThreadPerTaskExecutor();
        serviceClasses = new HashSet<>();
        dependencyClasses = new HashSet<>();
        ClassesLoader.load(serviceClasses, dependencyClasses, configs);
        dependencyManager = new DependencyManager(this);
    }

    public void mainLoop() throws IOException {
        serverSocket = new ServerSocket(21);
        while (!serverSocket.isClosed()) {
            Socket client = serverSocket.accept();
            sessions.submit(new Session(this, client));
        }
    }

    public void shutdown() {
        sessions.shutdown();
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
