package net.analyse.plugin.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BaseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public BaseEvent() {
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean call() {
        Bukkit.getServer().getPluginManager().callEvent(this);
        return this instanceof Cancellable && ((Cancellable)this).isCancelled();
    }
}