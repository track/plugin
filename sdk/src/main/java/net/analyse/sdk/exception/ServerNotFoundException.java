package net.analyse.sdk.exception;

/**
 * Exception thrown when a server is not found.
 */
public class ServerNotFoundException extends Throwable {
    public String getMessage() {
        return "That server doesn't exist!";
    }
}
