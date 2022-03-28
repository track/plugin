package net.analyse.sdk.exception;

public class InvalidIPAddressException extends Exception {

    private String ip;
    public InvalidIPAddressException(String ip)
    {
        this.ip = ip;
    }

    @Override
    public String getLocalizedMessage() {
        return "The IP address " + this.ip + " doesn't exist.";
    }
}
