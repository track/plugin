package net.analyse.sdk.exception;

public class ServerNotFoundException extends Exception {

    @Override
    public String getLocalizedMessage() {
        return "That server doesn't exist.";
    }
}
