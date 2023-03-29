package net.analyse.plugin.command.sub;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.command.SubCommand;
import net.analyse.sdk.SDK;
import net.analyse.sdk.platform.PlatformConfig;
import net.analyse.sdk.exception.ServerNotFoundException;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class SetupCommand extends SubCommand {
    public SetupCommand(AnalysePlugin platform) {
        super(platform, "setup", "analyse.setup");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage("§8[Analyse] §7Usage: /analyse setup <serverToken>");
            return;
        }

        String serverToken = args[0];
        AnalysePlugin platform = getPlatform();

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
                sender.sendMessage("§8[Analyse] §7Failed to save config: " + e.getMessage());
            }

            sender.sendMessage("§8[Analyse] §7Connected to §b" + serverInformation.getName() + "§7.");
            platform.configure();
            platform.getSDK().completeServerSetup();
        }).exceptionally(ex -> {
            Throwable cause = ex.getCause();

            if(cause instanceof ServerNotFoundException) {
                sender.sendMessage("§8[Analyse] §7Server not found. Please check your server token.");
                platform.halt();
            } else {
                sender.sendMessage("§8[Analyse] §cAn error occurred: " + cause.getMessage());
            }

            return null;
        });
    }
}
