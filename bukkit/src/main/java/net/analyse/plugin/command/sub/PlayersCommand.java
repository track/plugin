package net.analyse.plugin.command.sub;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.command.SubCommand;
import net.analyse.sdk.platform.PlatformConfig;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class PlayersCommand extends SubCommand {
    public PlayersCommand(AnalysePlugin platform) {
        super(platform, "players", "analyse.admin");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        getPlatform().getPlayers().values().forEach(player -> {
            getPlatform().log("[PLAYER] " + player.toString());
        });
    }
}
