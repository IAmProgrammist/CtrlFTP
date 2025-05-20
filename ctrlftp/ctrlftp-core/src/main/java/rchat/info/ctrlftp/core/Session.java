package rchat.info.ctrlftp.core;

import rchat.info.ctrlftp.core.dependencies.DependencyLevel;
import rchat.info.ctrlftp.core.dependencies.DependencyManager;
import rchat.info.ctrlftp.core.reflections.MethodResolver;
import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * A class that stores current session
 */
public class Session implements Runnable {
    private Server serverContext;
    private Socket client;
    private StringBuilder inputBuffer;
    private DependencyManager dependencyManager;

    public Session(Server serverContext, Socket client) {
        this.serverContext = serverContext;
        this.client = client;
        this.inputBuffer = new StringBuilder();
        this.dependencyManager = new DependencyManager(serverContext,
                this,
                DependencyLevel.SESSION,
                serverContext.getDependencyManager());
    }

    /**
     * A method that finds required method,
     * injects dependencies, launches method and
     * sends back response
     *
     * @param command a raw command from a user
     */
    private Response launchMethod(String command) {
        try {
            var optionalMethod = MethodResolver.findMethod(this.serverContext, command.split(" ")[0]);
            if (optionalMethod.isEmpty()) {
                return new Response(ResponseTypes.NOT_IMPLEMENTED, "Method not implemented");
            }
            var targetMethod = optionalMethod.get();

            DependencyManager local = new DependencyManager(
                    serverContext,
                    this,
                    command,
                    this.dependencyManager);
            var methodParameters = local.getDependenciesForParameters(targetMethod.getParameters());

            return (Response) targetMethod.invoke(null, methodParameters.toArray());
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(ResponseTypes.REQUESTED_ACTION_NOT_TAKEN);
        }
    }

    /**
     * A method that gets result from method launching and sends it to a user
     *
     * @param command a raw command from a user
     */
    private void processCommand(String command) throws IOException {
        sendResponse(launchMethod(command));
    }

    private void sendResponse(Response response) throws IOException {
        if (client.isClosed()) return;

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        writer.write(response.serialize().toString());
        writer.flush();
    }

    /**
     * Disconnects from the current user
     */
    public void disconnect() {
        try {
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * A main method that buffers user input and distinguishes raw command lines
     */
    @Override
    public void run() {
        try {
            sendResponse(new Response(ResponseTypes.COMMAND_OK, "Connected succesfully"));
            BufferedReader r = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            char[] buffer = new char[1024];
            int read;
            while ((read = r.read(buffer)) != -1) {
                inputBuffer.append(new String(buffer, 0, read));

                int newLinePosition;
                while ((newLinePosition = inputBuffer.indexOf("\r\n")) != -1) {
                    processCommand(inputBuffer.substring(0, newLinePosition));
                    inputBuffer = new StringBuilder(inputBuffer.substring(newLinePosition + 2));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
