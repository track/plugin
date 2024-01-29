package io.tebex.analytics.command.sub;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.tebex.analytics.AnalyticsPlugin;
import io.tebex.analytics.command.SubCommand;
import io.tebex.analytics.sdk.platform.PlatformConfig;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class DebugCommand extends SubCommand {
    public DebugCommand(AnalyticsPlugin platform) {
        super(platform, "debug", "analyse.admin");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        AnalyticsPlugin platform = getPlatform();
        PlatformConfig analyseConfig = platform.getPlatformConfig();

        boolean debugEnabled = args.length > 0 ? Boolean.parseBoolean(args[0]) : !analyseConfig.hasDebugEnabled();

        sender.sendMessage("§8[Analytics] §7Debug Mode: §f" + (debugEnabled ? "Enabled" : "Disabled") + "§7.");

        YamlDocument configFile = analyseConfig.getYamlDocument();
        configFile.set("debug", debugEnabled);
        analyseConfig.setDebugEnabled(debugEnabled);

        try {
            configFile.save();
        } catch (IOException e) {
            sender.sendMessage("§8[Analytics] §7Failed to save config: " + e.getMessage());
        }
    }
}
