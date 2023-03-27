package net.analyse.sdk.request.exception;

/**
 * Exception thrown when an error occurs during the request.
 */
public class AnalyseException extends Throwable {
    private final String message;

    public AnalyseException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
