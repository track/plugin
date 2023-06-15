package net.analyse.plugin.event.type;


import net.analyse.plugin.event.CancellableEvent;
import net.analyse.sdk.obj.AnalysePlayer;
import org.bukkit.entity.Player;

/**
 * The AnalysePlayerQuitEvent is called as the {@link AnalysePlayer} quits.
 * Cancelling the event will result in no tracked data being sent to the web API.
 */
public class AnalysePlayerQuitEvent extends CancellableEvent {

    private final Player bukkitPlayer;
    private final AnalysePlayer player;

    public AnalysePlayerQuitEvent(Player bukkitPlayer, AnalysePlayer player) {
        this.bukkitPlayer = bukkitPlayer;
        this.player = player;
    }

    public Player getBukkitPlayer() {
        return bukkitPlayer;
    }

    public AnalysePlayer getPlayer() {
        return player;
    }
}
