package rchat.info.ctrlftp.examplebasic;

import rchat.info.ctrlftp.core.Server;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        var u = new Server(List.of("dependencies.xml"));
        u.mainLoop();
    }
}
