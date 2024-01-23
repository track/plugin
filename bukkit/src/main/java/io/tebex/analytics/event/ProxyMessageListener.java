package io.tebex.analytics.event;

import io.tebex.analytics.AnalyticsPlugin;
import io.tebex.analytics.sdk.obj.AnalysePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class ProxyMessageListener implements PluginMessageListener {
    private final AnalyticsPlugin platform;

    public ProxyMessageListener(AnalyticsPlugin platform) {
        this.platform = platform;
    }

    public void register() {
        platform.getServer().getMessenger().registerOutgoingPluginChannel(platform, "analyse:proxy");
        platform.getServer().getMessenger().registerIncomingPluginChannel(platform, "analyse:proxy", this);
    }

    public void unregister() {
        platform.getServer().getMessenger().unregisterOutgoingPluginChannel(platform, "analyse:proxy");
        platform.getServer().getMessenger().unregisterIncomingPluginChannel(platform, "analyse:proxy", this);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player bukkitPlayer, @NotNull byte[] message) {
        if (!channel.equals("analyse:proxy")) return;
        AnalysePlayer player = platform.getPlayers().get(bukkitPlayer.getUniqueId());
        if (player == null) return;

        player.setDomain(new String(message));
        platform.debug("Received plugin message for " + player.getName() + " who joined from: " + player.getDomain());
    }
}
