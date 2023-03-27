package net.analyse.sdk.exception;

/**
 * Exception thrown when a server is not setup.
 */
public class ServerNotSetupException extends Throwable {
    public String getMessage() {
        return "Analyse not setup!";
    }
}
