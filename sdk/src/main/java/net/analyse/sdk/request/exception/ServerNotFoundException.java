package net.analyse.sdk.request.exception;

/**
 * Exception thrown when a server is not found.
 */
public class ServerNotFoundException extends Throwable {
    private final String message;

    public ServerNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
