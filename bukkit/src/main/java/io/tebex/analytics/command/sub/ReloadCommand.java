package io.tebex.analytics.command.sub;

import dev.dejvokep.boostedyaml.YamlDocument;
import io.tebex.analytics.AnalyticsPlugin;
import io.tebex.analytics.command.SubCommand;
import io.tebex.analytics.sdk.platform.PlatformConfig;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class ReloadCommand extends SubCommand {
    public ReloadCommand(AnalyticsPlugin platform) {
        super(platform, "reload", "analyse.admin");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        AnalyticsPlugin platform = getPlatform();
        try {
            YamlDocument configYaml = platform.initPlatformConfig();
            PlatformConfig config = platform.loadPlatformConfig(configYaml);

            if(config.hasProxyModeEnabled()) {
                platform.getProxyMessageListener().register();
            } else {
                platform.getProxyMessageListener().unregister();
            }

            sender.sendMessage("§8[Analytics] §7Successfully reloaded.");
        } catch (IOException e) {
            sender.sendMessage("§8[Analytics] §cFailed to reload the plugin: Check Console.");
            throw new RuntimeException(e);
        }


    }
}
