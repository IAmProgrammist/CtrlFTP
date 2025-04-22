package rchat.info.ctrlftp.core;

import rchat.info.ctrlftp.core.responses.Response;
import rchat.info.ctrlftp.core.responses.ResponseTypes;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * A class that stores current session
 */
public class Session implements Runnable {
    private Server serverContext;
    private Socket client;
    private StringBuilder inputBuffer;

    public Session(Server serverContext, Socket client) { }

    /**
     * A method that finds required method,
     * injects dependencies, launches method and
     * sends back response
     *
     * @param command a raw command from a user
     */
    private Response launchMethod(String command) {
        var optionalMethod = MethodResolver.findMethod(this.serverContext, command.split(" ")[0]);
        if (optionalMethod.isEmpty()) {
            return new Response(ResponseTypes.NOT_IMPLEMENTED);
        }

        // 2. Find required dependencies and inject them
        // 3. Launch method

        return new Response(ResponseTypes.COMMAND_OK);
    }

    /**
     * A method that gets result from method launching and sends it to a user
     *
     * @param command a raw command from a user
     */
    private void processCommand(String command) throws IOException {
        Response response = launchMethod(command);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        writer.write(response.serialize().toString());
    }

    /**
     * A main method that buffers user input and distinguishes raw command lines
     */
    @Override
    public void run() {
        try {
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
