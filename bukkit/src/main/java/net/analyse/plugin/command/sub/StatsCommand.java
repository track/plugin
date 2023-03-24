package net.analyse.plugin.command.sub;

import net.analyse.plugin.AnalysePlugin;
import net.analyse.plugin.command.SubCommand;
import net.analyse.sdk.obj.AnalysePlayer;
import net.analyse.sdk.platform.PlayerType;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatsCommand extends SubCommand {

    public StatsCommand(AnalysePlugin platform) {
        super(platform, "stats", "analyse.admin");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Collection<AnalysePlayer> players = getPlatform().getPlayers().values();

        Stream<AnalysePlayer> javaStream = players.stream().filter(analysePlayer -> analysePlayer.getType() == PlayerType.JAVA);
        Stream<AnalysePlayer> bedrockStream = players.stream().filter(analysePlayer -> analysePlayer.getType() == PlayerType.BEDROCK);

        int javaCount = (int) javaStream.count();
        int bedrockCount = (int) bedrockStream.count();
        int totalCount = javaCount + bedrockCount;

        sender.sendMessage("§b[Analyse] §7Domain Stats:");
        sender.sendMessage("§7");

        // Group by domain and show counts
        Map<String, Map<PlayerType, Long>> domainCounts = players.stream()
                .collect(Collectors.groupingBy(AnalysePlayer::getDomain,
                        Collectors.groupingBy(AnalysePlayer::getType, Collectors.counting())));

        Map<String, Long> domainTotals = domainCounts.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().values().stream().mapToLong(Long::longValue).sum()));

        Map<String, Map<PlayerType, Long>> topDomains = domainCounts.entrySet().stream()
                .sorted(Comparator.comparingLong((Map.Entry<String, Map<PlayerType, Long>> e) -> e.getValue().getOrDefault(PlayerType.JAVA, 0L) + e.getValue().getOrDefault(PlayerType.BEDROCK, 0L)).reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, LinkedHashMap::new));

        if(topDomains.size() > 0) {
            for (Map.Entry<String, Map<PlayerType, Long>> entry : topDomains.entrySet()) {
                String domain = entry.getKey();
                Map<PlayerType, Long> counts = entry.getValue();
                long domainTotal = domainTotals.get(domain);
                long javaDomainCount = counts.getOrDefault(PlayerType.JAVA, 0L);
                long bedrockDomainCount = counts.getOrDefault(PlayerType.BEDROCK, 0L);

                // Create a tooltip with the domain stats using chatcomponents
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {
                        new net.md_5.bungee.api.chat.TextComponent("§b" + domain + "\n"),

                        new net.md_5.bungee.api.chat.TextComponent("\n§7⚡ §7Java: §f" + javaDomainCount),
                        new net.md_5.bungee.api.chat.TextComponent("\n§7⚡ §7Bedrock: §f" + bedrockDomainCount),

                        new net.md_5.bungee.api.chat.TextComponent("\n\n§7Click for more information"),
                });

                if(sender instanceof Player) {
                    TextComponent component = new TextComponent("§7⚡ §b" + domain + ": §f" + domainTotal + " §7online §8§o(Hover for details)");
                    component.setHoverEvent(hoverEvent);

                    ((Player) sender).spigot().sendMessage(component);
                }

//                sender.sendMessage("§7⚡ §b" + domain + ": " + domainTotal + " §7online (§f" + javaDomainCount + " §7Java, §f" + bedrockDomainCount + " §7Bedrock)");
            }
        } else {
            sender.sendMessage("§7⚡ §7No domains found.");
        }

        sender.sendMessage("§7");
        sender.sendMessage("§b[Analyse] §7A total of " + totalCount + " players online.");
    }
}
