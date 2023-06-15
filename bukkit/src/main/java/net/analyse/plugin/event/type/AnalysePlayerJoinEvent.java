package net.analyse.plugin.event.type;


import net.analyse.plugin.event.CancellableEvent;
import net.analyse.sdk.obj.AnalysePlayer;
import org.bukkit.entity.Player;

/**
 * The AnalysePlayerJoinEvent is called before the {@link AnalysePlayer} object is stored.
 * Cancelling the event will result in no tracking of the player.
 */
public class AnalysePlayerJoinEvent extends CancellableEvent {

    private final Player bukkitPlayer;

    public AnalysePlayerJoinEvent(Player bukkitPlayer) {
        this.bukkitPlayer = bukkitPlayer;
    }

    public Player getBukkitPlayer() {
        return bukkitPlayer;
    }
}
