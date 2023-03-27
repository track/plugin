package net.analyse.sdk.platform;

import net.analyse.sdk.Analyse;
import net.analyse.sdk.platform.command.PlatformCommand;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a module of the platform.
 *
 * @author Analyse
 * @version 2.0.0
 */
public abstract class PlatformModule {
    private final Map<String, PlatformCommand> commands = new LinkedHashMap<>();

    /**
     * Returns the name of this module.
     * @return The name of this module.
     */
    public abstract String getName();

    /**
     * Registers the module.
     */
    public abstract void onEnable();

    /**
     * Unregisters the module.
     */
    public abstract void onDisable();

    /**
     * Returns a list of all dependencies of this module.
     * @return A list of all dependencies of this module.
     */
    @NotNull
    public abstract List<String> getDependencies();

    /**
     * Returns the platform of this module.
     * @return The platform of this module.
     */
    public Platform getPlatform() {
        return Analyse.get();
    }

    /**
     * Registers a command to this module.
     * @param command The command to register.
     */
    public void registerCommand(PlatformCommand command) {

        // Check if the command is already registered
        if (commands.containsKey(command.getName())) {
            throw new IllegalArgumentException(String.format("Module %s already has a command with the name %s", getName(), command.getName()));
        }

        // Register the command
        commands.put(command.getName(), command);
    }

    /**
     * Executes a command registered by this module.
     * @param commandName The name of the command to execute.
     * @param platformCommandSender The sender of the command.
     * @param commandArguments The arguments of the command.
     */
    public void executeCommand(String commandName, Object platformCommandSender, String[] commandArguments) {
        PlatformCommand command = commands.get(commandName);

        // Check if the command is registered
        if (command != null) {
            command.execute(platformCommandSender, commandArguments);
        } else {
            throw new IllegalArgumentException(String.format("Module %s does not have a command with the name %s", getName(), commandName));
        }
    }

    /**
     * Returns a list of all commands registered by this module.
     * @return A list of all commands registered by this module.
     */
    public List<PlatformCommand> getCommands() {
        return new ArrayList<>(commands.values());
    }
}
