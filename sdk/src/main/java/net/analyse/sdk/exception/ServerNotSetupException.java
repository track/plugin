package net.analyse.sdk.exception;

public class ServerNotSetupException extends Throwable {
    public String getMessage() {
        return "Analyse not setup!";
    }
}
