package net.analyse.sdk.platform.command;

import java.util.function.Consumer;

public class PlatformCommand {
    private final String name;
    private final String description;
    private final Consumer<PlatformCommandContext> commandExecutor;

    public PlatformCommand(String name, String description, Consumer<PlatformCommandContext> commandExecutor) {
        this.name = name;
        this.description = description;
        this.commandExecutor = commandExecutor;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void execute(Object sender, String[] commandArguments) {
        commandExecutor.accept(new PlatformCommandContext(sender, commandArguments));
    }
}