package net.analyse.sdk.exception;

public class ServerNotFoundException extends Throwable {
    public String getMessage() {
        return "That server doesn't exist!";
    }
}
