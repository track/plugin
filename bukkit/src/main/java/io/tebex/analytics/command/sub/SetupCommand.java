package io.tebex.analytics.command.sub;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.tebex.analytics.AnalyticsPlugin;
import io.tebex.analytics.command.SubCommand;
import io.tebex.analytics.sdk.SDK;
import io.tebex.analytics.sdk.exception.ServerNotFoundException;
import io.tebex.analytics.sdk.platform.PlatformConfig;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class SetupCommand extends SubCommand {
    public SetupCommand(AnalyticsPlugin platform) {
        super(platform, "setup", "analyse.setup");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            getPlatform().sendMessage(sender, "Invalid usage. Usage: &f/analytics setup <serverToken>");
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
                getPlatform().sendMessage(sender, "&cFailed to setup the plugin. Check console for more information.");
                e.printStackTrace();
            }

            platform.getSDK().completeServerSetup().thenAccept(v -> {
                getPlatform().sendMessage(sender, "Connected to &b" + serverInformation.getName() + "&7.");
                platform.configure();
            }).exceptionally(ex -> {
                getPlatform().sendMessage(sender, "&cFailed to setup the plugin. Check console for more information.");
                ex.printStackTrace();
                return null;
            });
        }).exceptionally(ex -> {
            Throwable cause = ex.getCause();

            if(cause instanceof ServerNotFoundException) {
                getPlatform().sendMessage(sender, "&cNo server was found with the provided token. Please check the token and try again.");
                platform.halt();
                return null;
            }

            getPlatform().sendMessage(sender, "&cFailed to setup the plugin. Check console for more information.");
            cause.printStackTrace();
            return null;
        });
    }
}
