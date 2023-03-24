package net.analyse.plugin.command.sub;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.command.SubCommand;
import net.analyse.sdk.platform.PlatformConfig;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class DebugCommand extends SubCommand {
    public DebugCommand(AnalysePlugin platform) {
        super(platform, "debug", "analyse.admin");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        AnalysePlugin platform = getPlatform();
        PlatformConfig analyseConfig = platform.getPlatformConfig();

        boolean debugEnabled = args.length > 0 ? Boolean.parseBoolean(args[0]) : !analyseConfig.isDebugEnabled();

        sender.sendMessage("§8[Analyse] §7Debug Mode: §f" + (debugEnabled ? "Enabled" : "Disabled") + "§7.");

        YamlDocument configFile = analyseConfig.getYamlDocument();
        configFile.set("debug", debugEnabled);
        analyseConfig.setDebugEnabled(debugEnabled);

        try {
            configFile.save();
        } catch (IOException e) {
            sender.sendMessage("§8[Analyse] §7Failed to save config: " + e.getMessage());
        }
    }
}
