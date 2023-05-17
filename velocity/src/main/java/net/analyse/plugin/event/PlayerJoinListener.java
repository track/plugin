package net.analyse.plugin.event;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import net.analyse.plugin.AnalysePlugin;

public class PlayerJoinListener {
    private final AnalysePlugin plugin;

    public PlayerJoinListener(AnalysePlugin plugin) {
        this.plugin = plugin;
        plugin.getProxy().getEventManager().register(plugin, this);
    }

    @Subscribe
    public void onPostLogin(LoginEvent event) {
        Player player = event.getPlayer();

        player.getVirtualHost().ifPresent(virtualDomain -> {
            plugin.getPlayerDomains().put(player.getUniqueId(), virtualDomain.getHostName());
        });
    }
}
