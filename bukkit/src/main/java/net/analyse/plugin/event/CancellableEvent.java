package net.analyse.plugin.event;

import org.bukkit.event.Cancellable;

public class CancellableEvent extends BaseEvent implements Cancellable {

    private boolean cancelled;

    public CancellableEvent() {
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
