package net.analyse.sdk.request.exception;

public class ServerNotFoundException extends Throwable {
    private final String message;

    public ServerNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
