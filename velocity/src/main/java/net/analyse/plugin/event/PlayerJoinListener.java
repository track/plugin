package net.analyse.plugin.event;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import net.analyse.plugin.AnalysePlugin;
import net.analyse.sdk.util.MapperUtil;

import java.net.InetSocketAddress;

public class PlayerJoinListener {
    private final AnalysePlugin plugin;

    public PlayerJoinListener(AnalysePlugin plugin) {
        this.plugin = plugin;
        plugin.getProxy().getEventManager().register(plugin, this);
    }

    @Subscribe
    public void onPostLogin(LoginEvent event) {
        Player player = event.getPlayer();
        String ipAddress = player.getRemoteAddress().getAddress().getHostAddress();

        plugin.log(player.getUsername() + " has connected from IP address: " + ipAddress);

        InetSocketAddress virtualDomain = player.getVirtualHost().orElse(null);
        if (virtualDomain != null) {
            String hostName = MapperUtil.sanitiseDomainAddress(virtualDomain);

            plugin.log(player.getUsername() + " has connected from domain: " + hostName);
            plugin.getPlayerDomains().put(player.getUniqueId(), hostName);
        }
    }
}
