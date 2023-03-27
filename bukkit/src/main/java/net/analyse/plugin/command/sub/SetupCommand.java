package net.analyse.plugin.command.sub;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.sdk.SDK;
import net.analyse.sdk.exception.ServerNotFoundException;
import net.analyse.sdk.platform.PlatformConfig;
import net.analyse.sdk.platform.command.PlatformCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class SetupCommand extends PlatformCommand {

    public SetupCommand(AnalysePlugin platform) {
        super("setup", "Configure the server token key", commandContext -> {
            CommandSender sender = (CommandSender) commandContext.getSender();
            String[] args = commandContext.getArguments();

            // Your command logic goes here
            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[Analyse] &7Usage: /analyse setup <serverToken>"));
                return;
            }

            String serverToken = args[0];

            SDK analyse = platform.getSDK();
            PlatformConfig analyseConfig = platform.getPlatformConfig();
            YamlDocument configFile = analyseConfig.getYamlDocument();

            analyse.setServerToken(serverToken);

            platform.getSDK().getServerInformation().whenComplete((information, throwable) -> {
                if(throwable != null) {
                    if(throwable.getCause() instanceof ServerNotFoundException) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[Analyse] &7Server not found. Please check your server token."));
                        platform.getHeartbeatManager().stop();
                        return;
                    }

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&8[Analyse] &cAn error occurred: &f%s&c.", throwable.getMessage())));
                    return;
                }

                analyseConfig.setServerToken(serverToken);
                configFile.set("server.token", serverToken);

                try {
                    configFile.save();
                } catch (IOException e) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&8[Analyse] Failed to save config: %s", e.getMessage())));
                }

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&8[Analyse] &7Connected to &b%s&7.", information.getName())));
                platform.configure();
            });
        });
    }
}