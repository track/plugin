package io.tebex.analytics.event;

import io.tebex.analytics.AnalysePlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerSwitchListener implements Listener {
    private final AnalysePlugin plugin;

    public PlayerSwitchListener(AnalysePlugin plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String hostName = plugin.getPlayerDomains().get(player.getUniqueId());
        if (hostName == null) return;

        player.getServer().sendData("analyse:proxy", hostName.getBytes());
        plugin.log(player.getName() + " switched servers (connected from: " + hostName + ")");
    }
}
