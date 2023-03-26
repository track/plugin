package net.analyse.sdk.platform.command;

public class PlatformCommandContext {
    private final Object sender;
    private final String[] commandArguments;

    public PlatformCommandContext(Object sender, String[] commandArguments) {
        this.sender = sender;
        this.commandArguments = commandArguments;
    }

    public Object getSender() {
        return sender;
    }

    public String[] getArguments() {
        return commandArguments;
    }
}