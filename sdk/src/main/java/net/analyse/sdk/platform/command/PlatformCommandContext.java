package net.analyse.sdk.platform.command;

/**
 * Represents the context of a command.
 *
 * @author kylegrahammatzen (kgm)
 * @since 2.0.0
 */
public class PlatformCommandContext {
    private final Object sender;
    private final String[] commandArguments;

    /**
     * Creates a new PlatformCommandContext.
     * @param sender The sender of the command.
     * @param commandArguments The arguments of the command.
     */
    public PlatformCommandContext(Object sender, String[] commandArguments) {
        this.sender = sender;
        this.commandArguments = commandArguments;
    }

    /**
     * Returns the sender of the command.
     * @return The sender of the command.
     */
    public Object getSender() {
        return sender;
    }

    /**
     * Returns the arguments of the command.
     * @return The arguments of the command.
     */
    public String[] getArguments() {
        return commandArguments;
    }
}
