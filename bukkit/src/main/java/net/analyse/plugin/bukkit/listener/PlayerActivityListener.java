package net.analyse.plugin.bukkit.listener;

import net.analyse.plugin.bukkit.AnalysePlugin;
import net.analyse.plugin.bukkit.util.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class PlayerActivityListener implements Listener {

    private final AnalysePlugin plugin;

    public PlayerActivityListener(final @NotNull AnalysePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(final @NotNull PlayerLoginEvent event) {
        if (!plugin.isSetup()) return;

        if (Config.EXCLUDED_PLAYERS.contains(event.getPlayer().getUniqueId().toString())) return;

        if (Config.ADVANCED_MODE) return;

        String hostName = event.getHostname();

        if (hostName.indexOf('\0') != -1) {
            hostName = hostName.substring(0, hostName.indexOf('\0'));
        }

        plugin.debug("Player connecting via: " + hostName);
        plugin.getPlayerDomainMap().put(event.getPlayer().getUniqueId(), hostName);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Config.EXCLUDED_PLAYERS.contains(event.getPlayer().getUniqueId().toString())) return;

        plugin.debug("Tracking " + event.getPlayer().getName() + " to current time");
        plugin.getActiveJoinMap().put(event.getPlayer().getUniqueId(), new Date());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (!plugin.isSetup()) return;
        plugin.sendPlayerSessionInformation(event.getPlayer(), false);
    }
}
