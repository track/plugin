package net.analyse.plugin.event;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.analyse.plugin.AnalysePlugin;

public class PlayerSwitchListener {
    private final AnalysePlugin plugin;

    public PlayerSwitchListener(AnalysePlugin plugin) {
        this.plugin = plugin;
        plugin.getProxy().getEventManager().register(plugin, this);
    }

    @Subscribe
    public void onServerSwitch(ServerPostConnectEvent event) {
        Player player = event.getPlayer();
        String hostName = plugin.getPlayerDomains().get(player.getUniqueId());
        if (hostName == null) return;

        player.getCurrentServer().ifPresent(serverConnection -> {
            serverConnection.sendPluginMessage(plugin.getChannel(), hostName.getBytes());
        });
        plugin.log(player.getUsername() + " switched servers (connected from: " + hostName + ")");
    }
}
