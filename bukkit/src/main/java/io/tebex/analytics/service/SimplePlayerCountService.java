package io.tebex.analytics.service;

import io.tebex.analytics.sdk.service.PlayerCountService;
import org.bukkit.Bukkit;

public final class SimplePlayerCountService implements PlayerCountService {

    @Override
    public int getPlayerCount() {
        return Bukkit.getOnlinePlayers().size();
    }

}
