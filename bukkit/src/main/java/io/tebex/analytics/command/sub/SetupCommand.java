package io.tebex.analytics.command.sub;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.tebex.analytics.AnalyticsPlugin;
import io.tebex.analytics.command.SubCommand;
import io.tebex.analytics.sdk.SDK;
import io.tebex.analytics.sdk.platform.PlatformConfig;
import io.tebex.analytics.sdk.exception.ServerNotFoundException;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class SetupCommand extends SubCommand {
    public SetupCommand(AnalyticsPlugin platform) {
        super(platform, "setup", "analyse.setup");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage("§8[Analytics] §7Usage: /analyse setup <serverToken>");
            return;
        }

        String serverToken = args[0];
        AnalyticsPlugin platform = getPlatform();

        SDK analyse = platform.getSDK();
        PlatformConfig analyseConfig = platform.getPlatformConfig();
        YamlDocument configFile = analyseConfig.getYamlDocument();

        analyse.setServerToken(serverToken);

        platform.getSDK().getServerInformation().thenAccept(serverInformation -> {
            analyseConfig.setServerToken(serverToken);
            configFile.set("server.token", serverToken);

            try {
                configFile.save();
            } catch (IOException e) {
                sender.sendMessage("§8[Analytics] §7Failed to save config: " + e.getMessage());
            }

            sender.sendMessage("§8[Analytics] §7Connected to §b" + serverInformation.getName() + "§7.");
            platform.configure();
            platform.getSDK().completeServerSetup().thenAccept(v -> {
                sender.sendMessage("§8[Analytics] §7Setup complete.");
            }).exceptionally(ex -> {
                sender.sendMessage("§8[Analytics] §cAn error occurred: " + ex.getMessage());
                ex.printStackTrace();
                return null;
            });
        }).exceptionally(ex -> {
            Throwable cause = ex.getCause();

            if(cause instanceof ServerNotFoundException) {
                sender.sendMessage("§8[Analytics] §7Server not found. Please check your server token.");
                platform.halt();
                return null;
            }

            sender.sendMessage("§8[Analytics] §cAn error occurred: " + cause.getMessage());
            cause.printStackTrace();
            return null;
        });
    }
}
