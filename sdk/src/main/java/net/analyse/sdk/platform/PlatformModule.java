package net.analyse.sdk.platform;

import net.analyse.sdk.Analyse;
import net.analyse.sdk.platform.command.PlatformCommand;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class PlatformModule {

    private final Map<String, PlatformCommand> commands = new LinkedHashMap<>();

    public abstract String getName();
    public abstract void onEnable();
    public abstract void onDisable();

    @NotNull
    public abstract List<String> getDependencies();

    public Platform getPlatform() {
        return Analyse.get();
    }

    public void registerCommand(PlatformCommand command) {

        if (commands.containsKey(command.getName())) {
            throw new IllegalArgumentException(String.format("Module %s already has a command with the name %s", getName(), command.getName()));
        }

        commands.put(command.getName(), command);
    }

    public void executeCommand(String commandName, Object platformCommandSender, String[] commandArguments) {
        PlatformCommand command = commands.get(commandName);
        if (command != null) {
            command.execute(platformCommandSender, commandArguments);
        } else {
            throw new IllegalArgumentException(String.format("Module %s does not have a command with the name %s", getName(), commandName));
        }
    }

    public List<PlatformCommand> getCommands() {
        return new ArrayList<>(commands.values());
    }
}
