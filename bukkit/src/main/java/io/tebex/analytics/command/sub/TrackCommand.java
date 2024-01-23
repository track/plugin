package io.tebex.analytics.command.sub;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.tebex.analytics.AnalyticsPlugin;
import io.tebex.analytics.command.SubCommand;
import io.tebex.analytics.sdk.obj.AnalysePlayer;
import io.tebex.analytics.sdk.obj.PlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

public class TrackCommand extends SubCommand {

    public TrackCommand(AnalyticsPlugin platform) {
        super(platform, "track", "analyse.admin");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // /analyse track <player> <event> <json metadata>
        if (args.length < 3) {
            sender.sendMessage("§8[Analytics] §7Usage: /analyse track <player> <event> <json metadata>");
            return;
        }

        Player bukkitPlayer = Bukkit.getServer().getPlayer(args[0]);
        if (bukkitPlayer == null) {
            sender.sendMessage("§8[Analytics] §7Player not found.");
            return;
        }

        AnalysePlayer player = getPlatform().getPlayer(bukkitPlayer.getUniqueId());
        if (player == null) {
            sender.sendMessage("§8[Analytics] §7Player not tracked.");
            return;
        }

        String[] namespace = args[1].split(":", 2);
        String origin = namespace[0];
        String eventName = namespace[1];

        String jsonMetadata = String.join(" ", Stream.of(args).skip(2).toArray(String[]::new));

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> fields = gson.fromJson(jsonMetadata, type);

        PlayerEvent event = new PlayerEvent(eventName, origin);
        for(Map.Entry<String, Object> entry : fields.entrySet()) {
            event.withMetadata(entry.getKey(), entry.getValue());
        }

        player.track(event);
        sender.sendMessage("§8[Analytics] §7Event tracked.");
    }
}
