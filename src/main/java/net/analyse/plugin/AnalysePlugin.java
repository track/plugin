package net.analyse.plugin;

import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.strategy.IdentityHashingStrategy;
import net.analyse.plugin.listener.PlayerActivityListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class AnalysePlugin extends JavaPlugin {

    private final Map<UUID, Date> activeJoinMap = new TCustomHashMap<>(new IdentityHashingStrategy<>());

    @Override
    public void onEnable() {
        new PlayerActivityListener(this);
    }

    @Override
    public void onDisable() {
        activeJoinMap.clear();
    }

    public Map<UUID, Date> getActiveJoinMap() {
        return activeJoinMap;
    }
}
