package net.analyse.plugin.command.sub;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.command.SubCommand;
import net.analyse.sdk.platform.PlatformConfig;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class ReloadCommand extends SubCommand {
    public ReloadCommand(AnalysePlugin platform) {
        super(platform, "reload", "analyse.admin");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        AnalysePlugin platform = getPlatform();
        try {
            YamlDocument configYaml = platform.initPlatformConfig();
            PlatformConfig config = platform.loadPlatformConfig(configYaml);

            if(config.hasProxyModeEnabled()) {
                platform.getProxyMessageListener().register();
            } else {
                platform.getProxyMessageListener().unregister();
            }

            sender.sendMessage("§8[Analyse] §7Successfully reloaded.");
        } catch (IOException e) {
            sender.sendMessage("§8[Analyse] §cFailed to reload the plugin: Check Console.");
            throw new RuntimeException(e);
        }


    }
}
