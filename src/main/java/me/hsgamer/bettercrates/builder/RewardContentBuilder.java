package me.hsgamer.bettercrates.builder;

import me.hsgamer.bettercrates.api.RewardContent;
import me.hsgamer.bettercrates.reward.CommandReward;
import me.hsgamer.bettercrates.reward.ItemReward;
import me.hsgamer.hscore.builder.MassBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class RewardContentBuilder extends MassBuilder<Map<String, Object>, RewardContent> {
    public static final RewardContentBuilder INSTANCE = new RewardContentBuilder();

    private RewardContentBuilder() {
        register(CommandReward::new, "command", "");
        register(ItemReward::new, "item");
    }

    public void register(Function<Map<String, Object>, RewardContent> creator, String... name) {
        register(input -> {
            String type = Objects.toString(input.get("content-type"), "");
            for (String s : name) {
                if (s.equalsIgnoreCase(type)) {
                    return Optional.of(creator.apply(input));
                }
            }
            return Optional.empty();
        });
    }
}
