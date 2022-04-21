package me.hsgamer.bettercrates.builder;

import me.hsgamer.bettercrates.api.reward.RewardContent;
import me.hsgamer.bettercrates.reward.CommandReward;
import me.hsgamer.hscore.builder.Builder;

import java.util.Map;
import java.util.Optional;

public class RewardContentBuilder extends Builder<Void, RewardContent> {
    public static final RewardContentBuilder INSTANCE = new RewardContentBuilder();

    private RewardContentBuilder() {
        register(v -> new CommandReward(), "command");
    }

    public RewardContent buildReward(Map<String, Object> map) {
        RewardContent rewardContent = Optional.ofNullable(map.get("type"))
                .map(String::valueOf)
                .flatMap(s -> build(s, null))
                .orElseGet(CommandReward::new);
        rewardContent.init(map);
        return rewardContent;
    }
}
