package net.analyse.sdk.platform.command;

import java.util.function.Consumer;

/**
 * Represents a command of the platform.
 */
public class PlatformCommand {
    private final String name;
    private final String description;
    private final Consumer<PlatformCommandContext> commandExecutor;

    /**
     * Creates a new platform command.
     *
     * @param name            The name of the command.
     * @param description     The description of the command.
     * @param commandExecutor The command consumer.
     */
    public PlatformCommand(String name, String description, Consumer<PlatformCommandContext> commandExecutor) {
        this.name = name;
        this.description = description;
        this.commandExecutor = commandExecutor;
    }

    /**
     * Returns the name of the command.
     *
     * @return The name of the command.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the command.
     *
     * @return The description of the command.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Executes the command consumer with the given arguments.
     *
     * @param sender           The sender of the command.
     * @param commandArguments The arguments of the command.
     */
    public void execute(Object sender, String[] commandArguments) {
        commandExecutor.accept(new PlatformCommandContext(sender, commandArguments));
    }
}