package net.analyse.plugin.command.sub;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.sdk.platform.PlatformConfig;
import net.analyse.sdk.platform.command.PlatformCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class DebugCommand extends PlatformCommand {

    public DebugCommand(AnalysePlugin platform) {
        super("debug", "Toggles Analyses plugin-wide debug mode.", commandContext -> {
            CommandSender sender = (CommandSender) commandContext.getSender();
            String[] args = commandContext.getArguments();

            PlatformConfig analyseConfig = platform.getPlatformConfig();

            boolean debugEnabled = args.length > 0 ? Boolean.parseBoolean(args[0]) : !analyseConfig.hasDebugEnabled();

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&8[Analyse] &7Debug Mode: &f%s&7.", debugEnabled ? "Enabled" : "Disabled")));

            YamlDocument configFile = analyseConfig.getYamlDocument();
            configFile.set("debug", debugEnabled);
            analyseConfig.setDebugEnabled(debugEnabled);

            try {
                configFile.save();
            } catch (IOException e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&8[Analyse] &7Failed to save config: &f%s&7.", e.getMessage())));
            }
        });
    }
}