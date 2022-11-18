package net.analyse.plugin.bukkit.listener;

import com.destroystokyo.paper.event.player.PlayerHandshakeEvent;
import net.analyse.plugin.bukkit.AnalysePlugin;
import net.analyse.plugin.bukkit.util.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerHandShakeListener implements Listener {
    private final AnalysePlugin plugin;

    public PlayerHandShakeListener(final @NotNull AnalysePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerHandShake(final @NotNull PlayerHandshakeEvent event) {
        UUID uuid = event.getUniqueId();

        if (uuid == null) return;

        if (!plugin.isSetup()) return;

        if (Config.EXCLUDED_PLAYERS.contains(uuid.toString())) return;

        if (Config.ADVANCED_MODE) return;

        plugin.debug("Player connecting via: " + event.getServerHostname());
        plugin.getPlayerDomainMap().put(uuid, event.getServerHostname());
    }

}
