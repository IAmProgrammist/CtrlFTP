package rchat.info.ctrlftp.core.responses;

/**
 * A Response class to send back to user some data
 */
public class Response {
    private ResponseTypes type;
    private String message = null;

    /**
     * A constructor of {@link Response}. You can use {@link Response#Response(ResponseTypes)}
     * to use default message for selected type
     *
     * @param type a return type
     * @param message a custom message
     */
    public Response(ResponseTypes type, String message) {
        this.type = type;
        this.message = message;
    }

    /**
     * A constructor of {@link Response}, a message is selected for specified
     * type automatically
     *
     * @param type a return type
     */
    public Response(ResponseTypes type) {
        this.type = type;
    }

    /**
     * A method to print by socket connection to user
     *
     * @return a server response in a form of string to by passed by Telnet
     */
    public StringBuilder serialize() {
        return new StringBuilder(
                String.valueOf(type.code) + " " +
                        message.replaceAll("\r{0,1}\n", " ") + "\r\n");
    }
}
