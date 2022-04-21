package me.hsgamer.bettercrates.crate;

import lombok.Value;
import me.hsgamer.bettercrates.api.reward.RewardContent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Value
public class Reward {
    String id;
    String displayName;
    int fakeChance;
    ItemStack displayItem;
    List<RewardContent> contents;
}
