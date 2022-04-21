package me.hsgamer.bettercrates.builder;

import me.hsgamer.bettercrates.BetterCrates;
import me.hsgamer.bettercrates.api.reward.RewardContent;
import me.hsgamer.bettercrates.reward.CommandReward;
import me.hsgamer.bettercrates.reward.ItemReward;
import me.hsgamer.hscore.builder.Builder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Optional;

public class RewardContentBuilder extends Builder<BetterCrates, RewardContent> {
    public static final RewardContentBuilder INSTANCE = new RewardContentBuilder();

    private RewardContentBuilder() {
        register(p -> new CommandReward(), "command");
        register(p -> new ItemReward(), "item");
    }

    public static RewardContent buildContent(Map<String, Object> map) {
        RewardContent rewardContent = Optional.ofNullable(map.get("content-type"))
                .map(String::valueOf)
                .flatMap(s -> INSTANCE.build(s, JavaPlugin.getPlugin(BetterCrates.class)))
                .orElseGet(CommandReward::new);
        rewardContent.init(map);
        return rewardContent;
    }
}
