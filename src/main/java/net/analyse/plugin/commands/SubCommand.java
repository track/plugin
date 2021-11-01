package net.analyse.plugin.commands;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.analyse.plugin.AnalysePlugin;
import org.bukkit.entity.Player;

@RequiredArgsConstructor @Getter
public abstract class SubCommand {

    @Getter(AccessLevel.NONE) protected final AnalysePlugin plugin;
    private final String name;
    private final String permission;

    public abstract void execute(Player player, String[] args);

}
